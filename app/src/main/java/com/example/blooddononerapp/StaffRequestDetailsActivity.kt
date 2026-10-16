package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StaffRequestDetailsActivity : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // =========================================================
    // UI
    // =========================================================

    private lateinit var txtRequestId: TextView
    private lateinit var txtBloodGroup: TextView
    private lateinit var txtUnits: TextView
    private lateinit var txtRequiredBefore: TextView
    private lateinit var txtRequester: TextView
    private lateinit var txtContact: TextView
    private lateinit var txtArea: TextView
    private lateinit var txtHospital: TextView
    private lateinit var txtUrgency: TextView
    private lateinit var txtAdditionalInfo: TextView

    private lateinit var btnVerify: MaterialButton
    private lateinit var btnFindDonors: MaterialButton
    private lateinit var btnFulfilled: MaterialButton
    private lateinit var btnReject: MaterialButton

    // =========================================================
    // REQUEST DATA
    // =========================================================

    private var requestDocumentId = ""
    private var currentStatus = "Pending"
    private var requestBloodGroup = ""
    private var requestArea = ""
    private var requestHospital = ""
    private var staffHospital = ""

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_request_details
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

        txtBloodGroup =
            findViewById(
                R.id.txtBloodGroup
            )

        txtUnits =
            findViewById(
                R.id.txtUnits
            )

        txtRequiredBefore =
            findViewById(
                R.id.txtRequiredBefore
            )

        txtRequester =
            findViewById(
                R.id.txtRequester
            )

        txtContact =
            findViewById(
                R.id.txtContact
            )

        txtArea =
            findViewById(
                R.id.txtArea
            )

        txtHospital =
            findViewById(
                R.id.txtHospital
            )

        txtUrgency =
            findViewById(
                R.id.txtUrgency
            )

        txtAdditionalInfo =
            findViewById(
                R.id.txtAdditionalInfo
            )

        btnVerify =
            findViewById(
                R.id.btnVerify
            )

        btnFindDonors =
            findViewById(
                R.id.btnFindDonors
            )

        btnFulfilled =
            findViewById(
                R.id.btnFulfilled
            )

        btnReject =
            findViewById(
                R.id.btnReject
            )

        // =====================================================
        // GET REQUEST DOCUMENT ID
        // =====================================================

        requestDocumentId =
            intent.getStringExtra(
                "REQUEST_DOCUMENT_ID"
            ) ?: ""

        if (requestDocumentId.isBlank()) {

            Toast.makeText(
                this,
                "Request ID not found",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }

        // =====================================================
        // VERIFY REQUEST
        // =====================================================

        btnVerify.setOnClickListener {

            showStatusConfirmation(
                newStatus = "Verified",
                title = "Verify Request?",
                message =
                    "Confirm that the emergency blood request details have been reviewed."
            )
        }

        // =====================================================
        // REJECT REQUEST
        // =====================================================

        btnReject.setOnClickListener {

            showStatusConfirmation(
                newStatus = "Rejected",
                title = "Reject Request?",
                message =
                    "Are you sure you want to reject this emergency request?"
            )
        }

        // =====================================================
        // FIND DONORS
        // =====================================================

        btnFindDonors.setOnClickListener {

            if (
                currentStatus.equals(
                    "Rejected",
                    ignoreCase = true
                ) ||
                currentStatus.equals(
                    "Fulfilled",
                    ignoreCase = true
                )
            ) {

                Toast.makeText(
                    this,
                    "This request cannot be matched",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            moveToMatchingAndOpenDonors()
        }

        // =====================================================
        // MARK AS FULFILLED
        // =====================================================

        btnFulfilled.setOnClickListener {

            if (!currentStatus.equals(
                    "Matching",
                    ignoreCase = true
                )
            ) {

                Toast.makeText(
                    this,
                    "Only matching requests can be fulfilled",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            AlertDialog.Builder(
                this
            )
                .setTitle(
                    "Mark Request as Fulfilled?"
                )
                .setMessage(
                    "Confirm that the required blood has been arranged for this request."
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Confirm"
                ) { _, _ ->

                    markRequestAsFulfilled()
                }
                .show()
        }

        // =====================================================
        // START
        // =====================================================

        loadStaffHospital()
    }

    // =========================================================
    // LOAD STAFF HOSPITAL
    // =========================================================

    private fun loadStaffHospital() {

        val user =
            auth.currentUser

        if (user == null) {

            Toast.makeText(
                this,
                "Staff account not found",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        firestore
            .collection(
                "users"
            )
            .document(
                user.uid
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
                    document
                        .getString(
                            "hospital"
                        )
                        ?.trim()
                        ?: ""

                if (
                    staffHospital.isBlank() ||
                    staffHospital.equals(
                        "Not Assigned",
                        ignoreCase = true
                    )
                ) {

                    Toast.makeText(
                        this,
                        "No hospital assigned to this staff account",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@addOnSuccessListener
                }

                loadRequestDetails()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load staff profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
    }

    // =========================================================
    // LOAD REQUEST DETAILS
    // =========================================================

    private fun loadRequestDetails() {

        firestore
            .collection(
                "emergencyRequests"
            )
            .document(
                requestDocumentId
            )
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Request not found",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@addOnSuccessListener
                }

                // =================================================
                // HOSPITAL
                // =================================================

                requestHospital =
                    document
                        .getString(
                            "hospital"
                        )
                        ?.trim()
                        ?: ""

                // =================================================
                // STAFF HOSPITAL SECURITY CHECK
                // =================================================

                if (!requestHospital.equals(
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

                // =================================================
                // REQUEST ID
                // =================================================

                val requestId =
                    document.getString(
                        "requestId"
                    ) ?: document.id

                // =================================================
                // BLOOD GROUP
                // =================================================

                requestBloodGroup =
                    document.getString(
                        "bloodGroup"
                    ) ?: "--"

                // =================================================
                // SAFE UNITS
                // =================================================

                val units =
                    when (
                        val value =
                            document.get(
                                "units"
                            )
                    ) {

                        is Number -> {

                            "${value.toLong()} Units"
                        }

                        is String -> {

                            val cleanValue =
                                value.trim()

                            if (
                                cleanValue.contains(
                                    "unit",
                                    ignoreCase = true
                                )
                            ) {

                                cleanValue

                            } else {

                                "$cleanValue Units"
                            }
                        }

                        else -> {

                            "--"
                        }
                    }

                // =================================================
                // REQUIRED DATE / TIME
                // =================================================

                val requiredDate =
                    document.getString(
                        "requiredDate"
                    ) ?: ""

                val requiredBefore =
                    document.getString(
                        "requiredBefore"
                    ) ?: "Not available"

                // =================================================
                // REQUESTER
                // =================================================

                val userEmail =
                    document.getString(
                        "userEmail"
                    ) ?: "Not available"

                // =================================================
                // CONTACT
                // =================================================

                val primaryContact =
                    document.getString(
                        "primaryContact"
                    ) ?: "Not available"

                val secondaryContact =
                    document.getString(
                        "secondaryContact"
                    ) ?: ""

                // =================================================
                // LOCATION
                // =================================================

                requestArea =
                    document.getString(
                        "area"
                    ) ?: ""

                val district =
                    document.getString(
                        "district"
                    ) ?: ""

                val province =
                    document.getString(
                        "province"
                    ) ?: ""

                // =================================================
                // URGENCY
                // =================================================

                val urgency =
                    document.getString(
                        "urgency"
                    ) ?: "Not available"

                // =================================================
                // ADDITIONAL INFO
                // =================================================

                val additionalInfo =
                    document.getString(
                        "additionalInfo"
                    ) ?: ""

                // =================================================
                // STATUS
                // =================================================

                currentStatus =
                    document.getString(
                        "status"
                    ) ?: "Pending"

                // =================================================
                // SET REQUEST ID
                // =================================================

                txtRequestId.text =
                    "REQ-${requestId.takeLast(8).uppercase()}"

                // =================================================
                // SET BLOOD GROUP
                // =================================================

                txtBloodGroup.text =
                    requestBloodGroup

                // =================================================
                // SET UNITS
                // =================================================

                txtUnits.text =
                    units

                // =================================================
                // SET REQUIRED BEFORE
                // =================================================

                txtRequiredBefore.text =

                    if (requiredDate.isNotBlank()) {

                        "$requiredDate • $requiredBefore"

                    } else {

                        requiredBefore
                    }

                // =================================================
                // SET REQUESTER
                // =================================================

                txtRequester.text =
                    userEmail

                // =================================================
                // SET CONTACT
                // =================================================

                txtContact.text =

                    if (secondaryContact.isBlank()) {

                        primaryContact

                    } else {

                        "$primaryContact / $secondaryContact"
                    }

                // =================================================
                // BUILD LOCATION
                // =================================================

                val location =
                    listOf(
                        requestArea,
                        district,
                        province
                    )
                        .filter {
                            it.isNotBlank()
                        }
                        .joinToString(
                            ", "
                        )

                txtArea.text =

                    if (location.isBlank()) {

                        "Not available"

                    } else {

                        location
                    }

                // =================================================
                // HOSPITAL
                // =================================================

                txtHospital.text =
                    requestHospital

                // =================================================
                // URGENCY
                // =================================================

                txtUrgency.text =
                    urgency

                // =================================================
                // ADDITIONAL INFO
                // =================================================

                txtAdditionalInfo.text =

                    if (additionalInfo.isBlank()) {

                        "No additional information"

                    } else {

                        additionalInfo
                    }

                // =================================================
                // UPDATE BUTTONS
                // =================================================

                updateButtons()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load request: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // UPDATE BUTTONS
    // =========================================================

    private fun updateButtons() {

        when (currentStatus.lowercase()) {

            // =================================================
            // PENDING
            // =================================================

            "pending" -> {

                btnVerify.visibility =
                    View.VISIBLE

                btnVerify.isEnabled =
                    true

                btnVerify.text =
                    "Verify Request"

                btnFindDonors.visibility =
                    View.GONE

                btnFulfilled.visibility =
                    View.GONE

                btnReject.visibility =
                    View.VISIBLE

                btnReject.isEnabled =
                    true
            }

            // =================================================
            // VERIFIED
            // =================================================

            "verified" -> {

                btnVerify.visibility =
                    View.GONE

                btnFindDonors.visibility =
                    View.VISIBLE

                btnFindDonors.isEnabled =
                    true

                btnFindDonors.text =
                    "Find Matching Donors"

                btnFulfilled.visibility =
                    View.GONE

                btnReject.visibility =
                    View.VISIBLE

                btnReject.isEnabled =
                    true
            }

            // =================================================
            // MATCHING
            // =================================================

            "matching" -> {

                btnVerify.visibility =
                    View.GONE

                btnFindDonors.visibility =
                    View.VISIBLE

                btnFindDonors.isEnabled =
                    true

                btnFindDonors.text =
                    "View / Find Matching Donors"

                btnFulfilled.visibility =
                    View.VISIBLE

                btnFulfilled.isEnabled =
                    true

                btnFulfilled.text =
                    "Mark as Fulfilled"

                btnReject.visibility =
                    View.VISIBLE

                btnReject.isEnabled =
                    true
            }

            // =================================================
            // FULFILLED
            // =================================================

            "fulfilled" -> {

                btnVerify.visibility =
                    View.GONE

                btnFindDonors.visibility =
                    View.GONE

                btnFulfilled.visibility =
                    View.GONE

                btnReject.visibility =
                    View.GONE
            }

            // =================================================
            // REJECTED
            // =================================================

            "rejected" -> {

                btnVerify.visibility =
                    View.GONE

                btnFindDonors.visibility =
                    View.GONE

                btnFulfilled.visibility =
                    View.GONE

                btnReject.visibility =
                    View.GONE
            }

            // =================================================
            // UNKNOWN STATUS
            // =================================================

            else -> {

                btnVerify.visibility =
                    View.GONE

                btnFindDonors.visibility =
                    View.GONE

                btnFulfilled.visibility =
                    View.GONE

                btnReject.visibility =
                    View.GONE
            }
        }
    }

    // =========================================================
    // VERIFY / REJECT CONFIRMATION
    // =========================================================

    private fun showStatusConfirmation(
        newStatus: String,
        title: String,
        message: String
    ) {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                title
            )
            .setMessage(
                message
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Confirm"
            ) { _, _ ->

                updateRequestStatus(
                    newStatus
                )
            }
            .show()
    }

    // =========================================================
    // UPDATE REQUEST STATUS
    // =========================================================

    private fun updateRequestStatus(
        newStatus: String
    ) {

        if (requestDocumentId.isBlank()) {

            return
        }

        btnVerify.isEnabled =
            false

        btnFindDonors.isEnabled =
            false

        btnFulfilled.isEnabled =
            false

        btnReject.isEnabled =
            false

        val updates =
            hashMapOf<String, Any>(
                "status" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )

        // =====================================================
        // VERIFIED
        // =====================================================

        if (newStatus.equals(
                "Verified",
                ignoreCase = true
            )
        ) {

            updates["verifiedAt"] =
                FieldValue.serverTimestamp()

            updates["verifiedBy"] =
                auth.currentUser?.uid ?: ""
        }

        // =====================================================
        // REJECTED
        // =====================================================

        if (newStatus.equals(
                "Rejected",
                ignoreCase = true
            )
        ) {

            updates["rejectedAt"] =
                FieldValue.serverTimestamp()

            updates["rejectedBy"] =
                auth.currentUser?.uid ?: ""
        }

        firestore
            .collection(
                "emergencyRequests"
            )
            .document(
                requestDocumentId
            )
            .update(
                updates
            )
            .addOnSuccessListener {

                currentStatus =
                    newStatus

                enableActionButtons()

                updateButtons()

                val successMessage =
                    when (newStatus) {

                        "Verified" ->
                            "Request verified successfully"

                        "Rejected" ->
                            "Request rejected"

                        else ->
                            "Request status updated"
                    }

                Toast.makeText(
                    this,
                    successMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                enableActionButtons()

                updateButtons()

                Toast.makeText(
                    this,
                    "Failed to update request: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // MOVE REQUEST TO MATCHING
    // =========================================================

    private fun moveToMatchingAndOpenDonors() {

        // =====================================================
        // ALREADY MATCHING
        // =====================================================

        if (
            currentStatus.equals(
                "Matching",
                ignoreCase = true
            )
        ) {

            openDonorMatching()

            return
        }

        // =====================================================
        // ONLY VERIFIED REQUESTS SHOULD START MATCHING
        // =====================================================

        if (!currentStatus.equals(
                "Verified",
                ignoreCase = true
            )
        ) {

            Toast.makeText(
                this,
                "Request must be verified before donor matching",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        btnFindDonors.isEnabled =
            false

        btnReject.isEnabled =
            false

        val updates =
            hashMapOf<String, Any>(
                "status" to "Matching",
                "matchingStartedAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
                "matchingStartedBy" to (auth.currentUser?.uid ?: "")
            )

        firestore
            .collection(
                "emergencyRequests"
            )
            .document(
                requestDocumentId
            )
            .update(
                updates
            )
            .addOnSuccessListener {

                currentStatus =
                    "Matching"

                enableActionButtons()

                updateButtons()

                openDonorMatching()
            }
            .addOnFailureListener { exception ->

                enableActionButtons()

                updateButtons()

                Toast.makeText(
                    this,
                    "Failed to start matching: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // MARK REQUEST AS FULFILLED
    // =========================================================

    private fun markRequestAsFulfilled() {

        if (requestDocumentId.isBlank()) {

            Toast.makeText(
                this,
                "Request ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Staff account not found",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // =====================================================
        // DISABLE ACTIONS
        // =====================================================

        btnVerify.isEnabled =
            false

        btnFindDonors.isEnabled =
            false

        btnFulfilled.isEnabled =
            false

        btnReject.isEnabled =
            false

        val requestReference =
            firestore
                .collection(
                    "emergencyRequests"
                )
                .document(
                    requestDocumentId
                )

        // =====================================================
        // RE-READ REQUEST
        // =====================================================

        requestReference
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    enableActionButtons()

                    Toast.makeText(
                        this,
                        "Request not found",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val latestHospital =
                    document
                        .getString(
                            "hospital"
                        )
                        ?.trim()
                        ?: ""

                val latestStatus =
                    document
                        .getString(
                            "status"
                        )
                        ?: ""

                // =================================================
                // HOSPITAL CHECK
                // =================================================

                if (!latestHospital.equals(
                        staffHospital,
                        ignoreCase = true
                    )
                ) {

                    enableActionButtons()

                    Toast.makeText(
                        this,
                        "This request does not belong to your hospital",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // =================================================
                // STATUS CHECK
                // =================================================

                if (!latestStatus.equals(
                        "Matching",
                        ignoreCase = true
                    )
                ) {

                    currentStatus =
                        latestStatus

                    enableActionButtons()

                    updateButtons()

                    Toast.makeText(
                        this,
                        "Only matching requests can be fulfilled",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // =================================================
                // UPDATE TO FULFILLED
                // =================================================

                val updates =
                    hashMapOf<String, Any>(
                        "status" to "Fulfilled",
                        "fulfilled" to true,
                        "fulfilledAt" to FieldValue.serverTimestamp(),
                        "fulfilledBy" to currentUser.uid,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )

                requestReference
                    .update(
                        updates
                    )
                    .addOnSuccessListener {

                        currentStatus =
                            "Fulfilled"

                        updateButtons()

                        Toast.makeText(
                            this,
                            "Request fulfilled successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { exception ->

                        enableActionButtons()

                        updateButtons()

                        Toast.makeText(
                            this,
                            "Failed to fulfill request: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { exception ->

                enableActionButtons()

                updateButtons()

                Toast.makeText(
                    this,
                    "Failed to verify request: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // ENABLE ACTION BUTTONS
    // =========================================================

    private fun enableActionButtons() {

        btnVerify.isEnabled =
            true

        btnFindDonors.isEnabled =
            true

        btnFulfilled.isEnabled =
            true

        btnReject.isEnabled =
            true
    }

    // =========================================================
    // OPEN DONOR MATCHING
    // =========================================================

    private fun openDonorMatching() {

        val intent =
            Intent(
                this,
                StaffDonorMatchingActivity::class.java
            )

        intent.putExtra(
            "REQUEST_DOCUMENT_ID",
            requestDocumentId
        )

        intent.putExtra(
            "REQUEST_ID",
            txtRequestId.text.toString()
        )

        intent.putExtra(
            "BLOOD_GROUP",
            requestBloodGroup
        )

        intent.putExtra(
            "AREA",
            requestArea
        )

        intent.putExtra(
            "HOSPITAL",
            requestHospital
        )

        startActivity(
            intent
        )
    }
}