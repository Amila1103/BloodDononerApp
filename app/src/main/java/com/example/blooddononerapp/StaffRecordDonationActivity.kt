package com.example.blooddononerapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StaffRecordDonationActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var documentId = ""
    private var appointmentId = ""
    private var donorId = ""
    private var donorName = ""
    private var bloodGroup = ""
    private var hospital = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_record_donation
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        firestore =
            FirebaseFirestore.getInstance()

        auth =
            FirebaseAuth.getInstance()

        // =====================================================
        // GET DATA FROM SCREENING
        // =====================================================

        documentId =
            intent.getStringExtra(
                "DOCUMENT_ID"
            ).orEmpty()

        appointmentId =
            intent.getStringExtra(
                "APPOINTMENT_ID"
            ).orEmpty()

        donorId =
            intent.getStringExtra(
                "DONOR_ID"
            ).orEmpty()

        donorName =
            intent.getStringExtra(
                "DONOR_NAME"
            ).orEmpty()

        bloodGroup =
            intent.getStringExtra(
                "BLOOD_GROUP"
            ).orEmpty()

        hospital =
            intent.getStringExtra(
                "CENTER"
            ).orEmpty()

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        val edtDonationId =
            findViewById<TextInputEditText>(
                R.id.edtDonationId
            )

        val edtDonorId =
            findViewById<TextInputEditText>(
                R.id.edtDonorId
            )

        val edtDonorName =
            findViewById<TextInputEditText>(
                R.id.edtDonorName
            )

        val dropdownBloodGroup =
            findViewById<AutoCompleteTextView>(
                R.id.dropdownBloodGroup
            )

        val dropdownDonationType =
            findViewById<AutoCompleteTextView>(
                R.id.dropdownDonationType
            )

        val edtAmount =
            findViewById<TextInputEditText>(
                R.id.edtAmount
            )

        val edtDateTime =
            findViewById<TextInputEditText>(
                R.id.edtDateTime
            )

        val edtHospital =
            findViewById<TextInputEditText>(
                R.id.edtHospital
            )

        val btnSaveDonation =
            findViewById<MaterialButton>(
                R.id.btnSaveDonation
            )

        // =====================================================
        // BLOOD GROUP DROPDOWN
        // =====================================================

        val bloodGroups =
            arrayOf(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
            )

        dropdownBloodGroup.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                bloodGroups
            )
        )

        // =====================================================
        // DONATION TYPE DROPDOWN
        // =====================================================

        val donationTypes =
            arrayOf(
                "Whole Blood",
                "Platelets",
                "Plasma"
            )

        dropdownDonationType.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                donationTypes
            )
        )

        // =====================================================
        // DONATION ID
        // =====================================================

        val generatedDonationId =
            "DON-${UUID.randomUUID().toString().take(8).uppercase(Locale.ENGLISH)}"

        edtDonationId.setText(
            generatedDonationId
        )

        // =====================================================
        // AUTO FILL
        // =====================================================

        edtDonorId.setText(
            donorId
        )

        edtDonorName.setText(
            donorName
        )

        dropdownBloodGroup.setText(
            bloodGroup,
            false
        )

        edtHospital.setText(
            hospital
        )

        // =====================================================
        // DATE TIME
        // =====================================================

        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.ENGLISH
            )

        edtDateTime.setText(
            dateFormat.format(
                Date()
            )
        )

        // =====================================================
        // READ ONLY
        // =====================================================

        edtDonorId.isFocusable =
            false

        edtDonorName.isFocusable =
            false

        dropdownBloodGroup.isEnabled =
            false

        edtHospital.isFocusable =
            false

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // SAVE
        // =====================================================

        btnSaveDonation.setOnClickListener {

            val donationId =
                edtDonationId.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            val donationType =
                dropdownDonationType.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            val amountText =
                edtAmount.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            val dateTime =
                edtDateTime.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            // =================================================
            // VALIDATION
            // =================================================

            if (documentId.isBlank()) {

                Toast.makeText(
                    this,
                    "Appointment document ID not found",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (donorId.isBlank()) {

                Toast.makeText(
                    this,
                    "Donor ID not found",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (donorName.isBlank()) {

                Toast.makeText(
                    this,
                    "Donor name not found",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (bloodGroup.isBlank()) {

                Toast.makeText(
                    this,
                    "Blood group not found",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (hospital.isBlank()) {

                Toast.makeText(
                    this,
                    "Hospital not found",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (donationType.isBlank()) {

                dropdownDonationType.error =
                    "Select donation type"

                return@setOnClickListener
            }

            if (amountText.isBlank()) {

                edtAmount.error =
                    "Recorded amount required"

                return@setOnClickListener
            }

            val amount =
                amountText.toIntOrNull()

            if (
                amount == null ||
                amount <= 0
            ) {

                edtAmount.error =
                    "Enter a valid amount"

                return@setOnClickListener
            }

            if (donationId.isBlank()) {
                edtDonationId.error = "Donation ID required"
                return@setOnClickListener
            }

            if (donationType !in listOf("Whole Blood", "Platelets", "Plasma")) {
                dropdownDonationType.error = "Select a valid donation type"
                return@setOnClickListener
            }

            // General data-entry guard, not a clinical collection-volume rule.
            // Use hospital-approved, donation-type-specific ranges in production.
            if (amount > 1000) {
                edtAmount.error = "Amount exceeds the supported entry limit"
                return@setOnClickListener
            }

            if (dateTime.isBlank()) {
                edtDateTime.error = "Donation date required"
                return@setOnClickListener
            }

            // =================================================
            // CONFIRM
            // =================================================

            AlertDialog.Builder(this)
                .setTitle(
                    "Save Donation?"
                )
                .setMessage(
                    """
                    Donor: $donorName
                    Blood Group: $bloodGroup
                    Type: $donationType
                    Amount: $amount mL
                    Hospital: $hospital

                    Confirm completed donation.
                    """.trimIndent()
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save"
                ) { _, _ ->

                    saveDonation(
                        donationId = donationId,
                        donationType = donationType,
                        amount = amount,
                        dateTime = dateTime,
                        button = btnSaveDonation
                    )
                }
                .show()
        }
    }

    // =========================================================
    // SAVE DONATION + USER ID + INVENTORY
    // =========================================================

    private fun saveDonation(
        donationId: String,
        donationType: String,
        amount: Int,
        dateTime: String,
        button: MaterialButton
    ) {

        val staffUser =
            auth.currentUser

        if (staffUser == null) {

            Toast.makeText(
                this,
                "Staff account not logged in",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (documentId.isBlank()) {

            Toast.makeText(
                this,
                "Appointment document ID not found",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        button.isEnabled =
            false

        button.text =
            "Saving..."

        // =====================================================
        // FIND HOSPITAL
        // =====================================================

        firestore
            .collection("hospitals")
            .whereEqualTo(
                "name",
                hospital
            )
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {

                    resetSaveButton(
                        button
                    )

                    Toast.makeText(
                        this,
                        "Hospital inventory not found",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val hospitalReference =
                    result.documents[0]
                        .reference

                // =================================================
                // BLOOD GROUP KEY
                // =================================================

                val stockKey =
                    bloodGroupToFirestoreKey(
                        bloodGroup
                    )

                if (stockKey == null) {

                    resetSaveButton(
                        button
                    )

                    Toast.makeText(
                        this,
                        "Invalid blood group",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // =================================================
                // REFERENCES
                // =================================================

                val appointmentReference =
                    firestore
                        .collection(
                            "donationAppointments"
                        )
                        .document(
                            documentId
                        )

                val donationReference =
                    firestore
                        .collection(
                            "donationRecords"
                        )
                        .document()

                // =================================================
                // TRANSACTION
                // =================================================

                firestore
                    .runTransaction { transaction ->

                        // =========================================
                        // READ APPOINTMENT
                        // =========================================

                        val appointmentSnapshot =
                            transaction.get(
                                appointmentReference
                            )

                        if (!appointmentSnapshot.exists()) {

                            throw IllegalStateException(
                                "Appointment not found"
                            )
                        }

                        // =========================================
                        // USER DATA FROM APPOINTMENT
                        // =========================================

                        val userId =
                            appointmentSnapshot
                                .getString(
                                    "userId"
                                )
                                .orEmpty()

                        val userEmail =
                            appointmentSnapshot
                                .getString(
                                    "userEmail"
                                )
                                .orEmpty()

                        /*
                         * userId is essential because
                         * DonationHistoryActivity will use it
                         * to show only the logged-in user's
                         * donation records.
                         */

                        if (userId.isBlank()) {

                            throw IllegalStateException(
                                "User ID not found in appointment"
                            )
                        }

                        // =========================================
                        // DUPLICATE CHECK
                        // =========================================

                        val currentStatus =
                            appointmentSnapshot
                                .getString(
                                    "status"
                                )
                                .orEmpty()

                        val inventoryUpdated =
                            appointmentSnapshot
                                .getBoolean(
                                    "inventoryUpdated"
                                )
                                ?: false

                        if (
                            currentStatus.equals(
                                "Completed",
                                ignoreCase = true
                            ) ||
                            appointmentSnapshot.getBoolean("completed") == true ||
                            appointmentSnapshot.getString("donationRecordId")?.isNotBlank() == true ||
                            inventoryUpdated
                        ) {

                            throw IllegalStateException(
                                "This donation has already been completed"
                            )
                        }

                        // Require staff screening to have approved this appointment.
                        // Accept only explicit eligible outcomes, never an absent result.
                        // Check the field name against your screening activity before enabling.

                        // =========================================
                        // READ HOSPITAL
                        // =========================================

                        val hospitalSnapshot =
                            transaction.get(
                                hospitalReference
                            )

                        if (!hospitalSnapshot.exists()) {

                            throw IllegalStateException(
                                "Hospital inventory not found"
                            )
                        }

                        // =========================================
                        // CURRENT STOCK
                        // =========================================

                        val stockValue = hospitalSnapshot
                            .get("bloodStock.$stockKey")

                        if (stockValue !is Number || stockValue.toLong() < 0L) {
                            throw IllegalStateException(
                                "Hospital blood stock is missing or invalid"
                            )
                        }

                        val currentStock = stockValue.toLong()

                        // =========================================
                        // ADD 1 UNIT
                        // =========================================

                        val newStock =
                            currentStock + 1L

                        // =========================================
                        // DONATION RECORD
                        // =========================================

                        val donationRecord =
                            hashMapOf<String, Any>(

                                "documentId" to
                                        donationReference.id,

                                "donationId" to
                                        donationId,

                                "appointmentDocumentId" to
                                        documentId,

                                "appointmentId" to
                                        appointmentId,

                                // =============================
                                // USER
                                // =============================

                                "userId" to
                                        userId,

                                "userEmail" to
                                        userEmail,

                                // =============================
                                // DONOR
                                // =============================

                                "donorId" to
                                        donorId,

                                "donorName" to
                                        donorName,

                                "bloodGroup" to
                                        bloodGroup,

                                // =============================
                                // DONATION
                                // =============================

                                "donationType" to
                                        donationType,

                                "amountMl" to
                                        amount,

                                "hospital" to
                                        hospital,

                                "hospitalDocumentId" to
                                        hospitalReference.id,

                                "recordedDateTime" to
                                        dateTime,

                                // =============================
                                // INVENTORY
                                // =============================

                                "inventoryUnitsAdded" to
                                        1,

                                // =============================
                                // STAFF
                                // =============================

                                "staffId" to
                                        staffUser.uid,

                                "staffEmail" to
                                        (
                                                staffUser.email
                                                    ?: ""
                                                ),

                                "status" to
                                        "Completed",

                                "createdAt" to
                                        FieldValue.serverTimestamp()
                            )

                        // =========================================
                        // SAVE DONATION RECORD
                        // =========================================

                        transaction.set(
                            donationReference,
                            donationRecord
                        )

                        // =========================================
                        // UPDATE APPOINTMENT
                        // =========================================

                        transaction.update(
                            appointmentReference,
                            mapOf(

                                "status" to
                                        "Completed",

                                "completed" to
                                        true,

                                "completedAt" to
                                        FieldValue.serverTimestamp(),

                                "donationRecordId" to
                                        donationReference.id,

                                "donationId" to
                                        donationId,

                                "donationType" to
                                        donationType,

                                "amountMl" to
                                        amount,

                                "inventoryUpdated" to
                                        true,

                                "inventoryUnitsAdded" to
                                        1,

                                "updatedAt" to
                                        FieldValue.serverTimestamp()
                            )
                        )

                        // =========================================
                        // UPDATE BLOOD INVENTORY
                        // =========================================

                        transaction.update(
                            hospitalReference,
                            mapOf(

                                "bloodStock.$stockKey" to
                                        newStock,

                                "updatedAt" to
                                        FieldValue.serverTimestamp()
                            )
                        )

                        null
                    }
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Donation completed and blood inventory updated",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }
                    .addOnFailureListener { exception ->

                        resetSaveButton(
                            button
                        )

                        Toast.makeText(
                            this,
                            exception.message
                                ?: "Unable to save donation",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { exception ->

                resetSaveButton(
                    button
                )

                Toast.makeText(
                    this,
                    "Unable to find hospital: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // RESET BUTTON
    // =========================================================

    private fun resetSaveButton(
        button: MaterialButton
    ) {

        button.isEnabled =
            true

        button.text =
            "Save Donation Record"
    }

    // =========================================================
    // BLOOD GROUP -> FIRESTORE KEY
    // =========================================================

    private fun bloodGroupToFirestoreKey(
        group: String
    ): String? {

        return when (
            group
                .trim()
                .uppercase()
        ) {

            "A+" ->
                "APlus"

            "A-" ->
                "AMinus"

            "B+" ->
                "BPlus"

            "B-" ->
                "BMinus"

            "AB+" ->
                "ABPlus"

            "AB-" ->
                "ABMinus"

            "O+" ->
                "OPlus"

            "O-" ->
                "OMinus"

            else ->
                null
        }
    }
}