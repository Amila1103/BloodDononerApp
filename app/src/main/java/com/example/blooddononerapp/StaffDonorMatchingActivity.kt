package com.example.blooddononerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StaffDonorMatchingActivity : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // =========================================================
    // UI
    // =========================================================

    private lateinit var txtRequestId: TextView
    private lateinit var txtRequiredBlood: TextView
    private lateinit var txtEmptyDonors: TextView
    private lateinit var donorContainer: LinearLayout

    // =========================================================
    // REQUEST DATA
    // =========================================================

    private var requestDocumentId = ""
    private var requestId = ""
    private var bloodGroup = ""
    private var requestArea = ""
    private var requestHospital = ""

    // =========================================================
    // STAFF DATA
    // =========================================================

    private var staffUid = ""
    private var staffHospital = ""

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_donor_matching
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        auth =
            FirebaseAuth.getInstance()

        firestore =
            FirebaseFirestore.getInstance()

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        txtRequestId =
            findViewById(
                R.id.txtRequestId
            )

        txtRequiredBlood =
            findViewById(
                R.id.txtRequiredBlood
            )

        txtEmptyDonors =
            findViewById(
                R.id.txtEmptyDonors
            )

        donorContainer =
            findViewById(
                R.id.donorContainer
            )

        // =====================================================
        // GET INTENT DATA
        // =====================================================

        requestDocumentId =
            intent.getStringExtra(
                "REQUEST_DOCUMENT_ID"
            ) ?: ""

        requestId =
            intent.getStringExtra(
                "REQUEST_ID"
            ) ?: "Request"

        bloodGroup =
            intent.getStringExtra(
                "BLOOD_GROUP"
            ) ?: ""

        requestArea =
            intent.getStringExtra(
                "AREA"
            ) ?: ""

        requestHospital =
            intent.getStringExtra(
                "HOSPITAL"
            ) ?: ""

        // =====================================================
        // SET HEADER
        // =====================================================

        txtRequestId.text =
            requestId

        txtRequiredBlood.text =
            if (bloodGroup.isBlank()) {
                "Blood Required"
            } else {
                "$bloodGroup Required"
            }

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }

        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        if (
            requestDocumentId.isBlank() ||
            bloodGroup.isBlank()
        ) {

            Toast.makeText(
                this,
                "Request information not found",
                Toast.LENGTH_LONG
            ).show()

            txtEmptyDonors.visibility =
                View.VISIBLE

            txtEmptyDonors.text =
                "Unable to load matching donors"

            return
        }

        // =====================================================
        // LOAD STAFF + DONORS
        // =====================================================

        loadStaffProfile()
    }

    // =========================================================
    // LOAD LOGGED-IN STAFF PROFILE
    // =========================================================

    private fun loadStaffProfile() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Staff account not found",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        staffUid =
            currentUser.uid

        firestore
            .collection(
                "users"
            )
            .document(
                currentUser.uid
            )
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Staff profile not found",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@addOnSuccessListener
                }

                val role =
                    document.getString(
                        "role"
                    ) ?: ""

                if (!role.equals(
                        "STAFF",
                        ignoreCase = true
                    )
                ) {

                    Toast.makeText(
                        this,
                        "Unauthorized account",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@addOnSuccessListener
                }

                staffHospital =
                    document.getString(
                        "hospital"
                    )
                        ?.trim()
                        ?: ""

                // Request එකත් staff hospital එකටම අදාළද බලනවා.
                if (
                    requestHospital.isNotBlank() &&
                    staffHospital.isNotBlank() &&
                    !requestHospital.equals(
                        staffHospital,
                        ignoreCase = true
                    )
                ) {

                    Toast.makeText(
                        this,
                        "This request does not belong to your hospital",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@addOnSuccessListener
                }

                loadMatchingDonors()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load staff profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // LOAD MATCHING DONORS
    // =========================================================

    private fun loadMatchingDonors() {

        donorContainer.removeAllViews()

        txtEmptyDonors.visibility =
            View.VISIBLE

        txtEmptyDonors.text =
            "Loading matching donors..."

        /*
         * Query එක simple කරලා තියෙන්නේ Firestore
         * composite indexes avoid කරන්න.
         *
         * bloodGroup එකෙන් Firebase query කරනවා.
         *
         * role == USER
         * availableToDonate == true
         *
         * checks app එකේ locally කරනවා.
         */

        firestore
            .collection(
                "users"
            )
            .whereEqualTo(
                "bloodGroup",
                bloodGroup
            )
            .get()
            .addOnSuccessListener { documents ->

                donorContainer.removeAllViews()

                var donorCount = 0

                for (document in documents) {

                    // =========================================
                    // ROLE
                    // =========================================

                    val role =
                        document.getString(
                            "role"
                        ) ?: ""

                    if (!role.equals(
                            "USER",
                            ignoreCase = true
                        )
                    ) {

                        continue
                    }

                    // =========================================
                    // AVAILABILITY
                    // =========================================

                    val available =
                        when (
                            val value =
                                document.get(
                                    "availableToDonate"
                                )
                        ) {

                            is Boolean ->
                                value

                            is String ->
                                value.equals(
                                    "true",
                                    ignoreCase = true
                                ) ||
                                        value.equals(
                                            "yes",
                                            ignoreCase = true
                                        )

                            else ->
                                false
                        }

                    if (!available) {

                        continue
                    }

                    // =========================================
                    // DONOR DATA
                    // =========================================

                    val donorUid =
                        document.id

                    val donorName =
                        document.getString(
                            "fullName"
                        )
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: "Donor"

                    val donorPhone =
                        document.getString(
                            "phone"
                        )
                            ?.trim()
                            ?: ""

                    val donorArea =
                        document.getString(
                            "area"
                        )
                            ?.trim()
                            ?: ""

                    val donorDistrict =
                        document.getString(
                            "district"
                        )
                            ?.trim()
                            ?: ""

                    val donorBloodGroup =
                        document.getString(
                            "bloodGroup"
                        )
                            ?: bloodGroup

                    /*
                     * Current user documents වල dedicated donorId
                     * field එකක් නැති වුණත් display ID එකක්
                     * Firebase UID එකෙන් generate කරනවා.
                     *
                     * Ownership සඳහා මේ display ID එක use කරන්නේ නෑ.
                     */

                    val donorDisplayId =
                        "D-${
                            donorUid
                                .take(6)
                                .uppercase()
                        }"

                    // =========================================
                    // PREVIOUS DONATIONS
                    // =========================================

                    loadDonationCountAndAddCard(
                        donorUid = donorUid,
                        donorName = donorName,
                        donorPhone = donorPhone,
                        donorArea = donorArea,
                        donorDistrict = donorDistrict,
                        donorBloodGroup = donorBloodGroup,
                        donorDisplayId = donorDisplayId
                    )

                    donorCount++
                }

                if (donorCount == 0) {

                    txtEmptyDonors.visibility =
                        View.VISIBLE

                    txtEmptyDonors.text =
                        "No available $bloodGroup donors found"

                } else {

                    txtEmptyDonors.visibility =
                        View.GONE
                }
            }
            .addOnFailureListener { exception ->

                donorContainer.removeAllViews()

                txtEmptyDonors.visibility =
                    View.VISIBLE

                txtEmptyDonors.text =
                    "Unable to load matching donors"

                Toast.makeText(
                    this,
                    "Failed to load donors: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // GET PREVIOUS DONATION COUNT
    // =========================================================

    private fun loadDonationCountAndAddCard(
        donorUid: String,
        donorName: String,
        donorPhone: String,
        donorArea: String,
        donorDistrict: String,
        donorBloodGroup: String,
        donorDisplayId: String
    ) {

        firestore
            .collection(
                "donationRecords"
            )
            .whereEqualTo(
                "userId",
                donorUid
            )
            .get()
            .addOnSuccessListener { records ->

                addDonorCard(
                    donorUid = donorUid,
                    donorName = donorName,
                    donorPhone = donorPhone,
                    donorArea = donorArea,
                    donorDistrict = donorDistrict,
                    donorBloodGroup = donorBloodGroup,
                    donorDisplayId = donorDisplayId,
                    previousDonations = records.size()
                )
            }
            .addOnFailureListener {

                // Donation count fail වුණත් donor card hide කරන්නේ නෑ.

                addDonorCard(
                    donorUid = donorUid,
                    donorName = donorName,
                    donorPhone = donorPhone,
                    donorArea = donorArea,
                    donorDistrict = donorDistrict,
                    donorBloodGroup = donorBloodGroup,
                    donorDisplayId = donorDisplayId,
                    previousDonations = 0
                )
            }
    }

    // =========================================================
    // ADD DYNAMIC DONOR CARD
    // =========================================================

    private fun addDonorCard(
        donorUid: String,
        donorName: String,
        donorPhone: String,
        donorArea: String,
        donorDistrict: String,
        donorBloodGroup: String,
        donorDisplayId: String,
        previousDonations: Int
    ) {

        val donorView =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.item_staff_matching_donor,
                    donorContainer,
                    false
                )

        // =====================================================
        // FIND ITEM VIEWS
        // =====================================================

        val txtDonorBloodGroup =
            donorView.findViewById<TextView>(
                R.id.txtDonorBloodGroup
            )

        val txtDonorName =
            donorView.findViewById<TextView>(
                R.id.txtDonorName
            )

        val txtDonorInfo =
            donorView.findViewById<TextView>(
                R.id.txtDonorInfo
            )

        val txtPreviousDonations =
            donorView.findViewById<TextView>(
                R.id.txtPreviousDonations
            )

        val btnContact =
            donorView.findViewById<MaterialButton>(
                R.id.btnContact
            )

        val btnNotify =
            donorView.findViewById<MaterialButton>(
                R.id.btnNotify
            )

        // =====================================================
        // SET BLOOD GROUP
        // =====================================================

        txtDonorBloodGroup.text =
            donorBloodGroup

        // =====================================================
        // SET NAME
        // =====================================================

        txtDonorName.text =
            donorName

        // =====================================================
        // LOCATION
        // =====================================================

        val donorLocation =
            listOf(
                donorArea,
                donorDistrict
            )
                .filter {
                    it.isNotBlank()
                }
                .distinct()
                .joinToString(
                    ", "
                )

        txtDonorInfo.text =

            if (donorLocation.isBlank()) {

                "Donor ID: $donorDisplayId"

            } else {

                "$donorLocation • Donor ID: $donorDisplayId"
            }

        // =====================================================
        // DONATION COUNT
        // =====================================================

        txtPreviousDonations.text =
            "Previous donations: $previousDonations"

        // =====================================================
        // CONTACT
        // =====================================================

        btnContact.setOnClickListener {

            if (donorPhone.isBlank()) {

                Toast.makeText(
                    this,
                    "Phone number not available",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                openDialer(
                    donorPhone
                )
            }
        }

        // =====================================================
        // NOTIFY
        // =====================================================

        btnNotify.setOnClickListener {

            showNotificationConfirmation(
                donorUid = donorUid,
                donorName = donorName,
                donorBloodGroup = donorBloodGroup,
                button = btnNotify
            )
        }

        // =====================================================
        // ADD CARD
        // =====================================================

        donorContainer.addView(
            donorView
        )
    }

    // =========================================================
    // OPEN PHONE DIALER
    // =========================================================

    private fun openDialer(
        phone: String
    ) {

        try {

            val intent =
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse(
                        "tel:${Uri.encode(phone)}"
                    )
                )

            startActivity(
                intent
            )

        } catch (exception: Exception) {

            Toast.makeText(
                this,
                "Unable to open phone dialer",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // NOTIFICATION CONFIRMATION
    // =========================================================

    private fun showNotificationConfirmation(
        donorUid: String,
        donorName: String,
        donorBloodGroup: String,
        button: MaterialButton
    ) {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Notify Donor?"
            )
            .setMessage(
                "Send an emergency donation request notification to $donorName?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Notify"
            ) { _, _ ->

                saveDonorNotification(
                    donorUid = donorUid,
                    donorName = donorName,
                    donorBloodGroup = donorBloodGroup,
                    button = button
                )
            }
            .show()
    }

    // =========================================================
    // SAVE NOTIFICATION TO FIRESTORE
    // =========================================================

    private fun saveDonorNotification(
        donorUid: String,
        donorName: String,
        donorBloodGroup: String,
        button: MaterialButton
    ) {

        button.isEnabled =
            false

        button.text =
            "Sending..."

        val notificationReference =
            firestore
                .collection(
                    "notifications"
                )
                .document()

        val notification =
            hashMapOf<String, Any>(

                // =============================================
                // NOTIFICATION ID
                // =============================================

                "notificationId" to
                        notificationReference.id,

                // =============================================
                // USER / DONOR
                // =============================================

                "userId" to
                        donorUid,

                "donorId" to
                        donorUid,

                "donorName" to
                        donorName,

                "donorBloodGroup" to
                        donorBloodGroup,

                // =============================================
                // REQUEST
                // =============================================

                "requestDocumentId" to
                        requestDocumentId,

                "requestId" to
                        requestId,

                "bloodGroup" to
                        bloodGroup,

                "area" to
                        requestArea,

                "hospital" to
                        requestHospital,

                // =============================================
                // STAFF
                // =============================================

                "sentBy" to
                        staffUid,

                "sentByRole" to
                        "STAFF",

                "staffHospital" to
                        staffHospital,

                // =============================================
                // NOTIFICATION CONTENT
                // =============================================

                "type" to
                        "EMERGENCY_BLOOD_REQUEST",

                "title" to
                        "Emergency Blood Request",

                "message" to
                        "$bloodGroup blood is urgently required at $requestHospital.",

                // =============================================
                // STATUS
                // =============================================

                "status" to
                        "Sent",

                "read" to
                        false,

                // =============================================
                // TIMESTAMP
                // =============================================

                "createdAt" to
                        FieldValue.serverTimestamp()
            )

        notificationReference
            .set(
                notification
            )
            .addOnSuccessListener {

                button.isEnabled =
                    false

                button.text =
                    "Notified"

                Toast.makeText(
                    this,
                    "$donorName notified successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                button.isEnabled =
                    true

                button.text =
                    "Notify Donor"

                Toast.makeText(
                    this,
                    "Failed to notify donor: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}