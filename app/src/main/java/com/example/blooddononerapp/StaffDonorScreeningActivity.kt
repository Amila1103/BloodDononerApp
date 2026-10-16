package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StaffDonorScreeningActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var rgFeelingWell: RadioGroup
    private lateinit var rgRecentIllness: RadioGroup
    private lateinit var rgMedication: RadioGroup
    private lateinit var rgRecentDonation: RadioGroup
    private lateinit var rgFinalDecision: RadioGroup

    private var documentId = ""
    private var appointmentId = ""
    private var donorName = ""
    private var donorId = ""
    private var bloodGroup = ""
    private var center = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_donor_screening
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        firestore =
            FirebaseFirestore.getInstance()

        // =====================================================
        // GET DATA FROM APPOINTMENT DETAILS
        // =====================================================

        documentId =
            intent.getStringExtra(
                "DOCUMENT_ID"
            ) ?: ""

        appointmentId =
            intent.getStringExtra(
                "APPOINTMENT_ID"
            ) ?: "APT-UNKNOWN"

        donorName =
            intent.getStringExtra(
                "DONOR_NAME"
            ) ?: "Donor"

        donorId =
            intent.getStringExtra(
                "DONOR_ID"
            ) ?: "D-UNKNOWN"

        bloodGroup =
            intent.getStringExtra(
                "BLOOD_GROUP"
            ) ?: "-"

        center =
            intent.getStringExtra(
                "CENTER"
            ) ?: ""

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        val txtDonorName =
            findViewById<TextView>(
                R.id.txtDonorName
            )

        val txtDonorId =
            findViewById<TextView>(
                R.id.txtDonorId
            )

        val txtBloodGroup =
            findViewById<TextView>(
                R.id.txtBloodGroup
            )

        val txtAppointmentId =
            findViewById<TextView>(
                R.id.txtAppointmentId
            )

        val edtNotes =
            findViewById<TextInputEditText>(
                R.id.edtScreeningNotes
            )

        val btnSave =
            findViewById<MaterialButton>(
                R.id.btnSaveScreening
            )

        rgFeelingWell =
            findViewById(
                R.id.rgFeelingWell
            )

        rgRecentIllness =
            findViewById(
                R.id.rgRecentIllness
            )

        rgMedication =
            findViewById(
                R.id.rgMedication
            )

        rgRecentDonation =
            findViewById(
                R.id.rgRecentDonation
            )

        rgFinalDecision =
            findViewById(
                R.id.rgFinalDecision
            )

        // =====================================================
        // SHOW REAL DONOR DATA
        // =====================================================

        txtDonorName.text =
            donorName

        txtDonorId.text =
            donorId

        txtBloodGroup.text =
            bloodGroup

        txtAppointmentId.text =
            appointmentId

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // SAVE SCREENING
        // =====================================================

        btnSave.setOnClickListener {

            if (!isAnswered(rgFeelingWell)) {

                showMessage(
                    "Please record whether the donor feels well today."
                )

                return@setOnClickListener
            }

            if (!isAnswered(rgRecentIllness)) {

                showMessage(
                    "Please record recent illness information."
                )

                return@setOnClickListener
            }

            if (!isAnswered(rgMedication)) {

                showMessage(
                    "Please record medication information."
                )

                return@setOnClickListener
            }

            if (!isAnswered(rgRecentDonation)) {

                showMessage(
                    "Please record recent donation information."
                )

                return@setOnClickListener
            }

            if (!isAnswered(rgFinalDecision)) {

                showMessage(
                    "Please record the staff eligibility decision."
                )

                return@setOnClickListener
            }

            val selectedDecision =
                getSelectedAnswer(
                    rgFinalDecision
                )

            val notes =
                edtNotes.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            AlertDialog.Builder(this)
                .setTitle(
                    "Save Screening Result?"
                )
                .setMessage(
                    """
                    Donor: $donorName
                    Appointment: $appointmentId

                    Decision: $selectedDecision

                    This records the screening result entered by hospital staff.
                    """.trimIndent()
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save"
                ) { _, _ ->

                    saveScreeningResult(
                        selectedDecision,
                        notes,
                        btnSave
                    )
                }
                .show()
        }
    }

    // =========================================================
    // SAVE SCREENING TO FIREBASE
    // =========================================================

    private fun saveScreeningResult(
        decision: String,
        notes: String,
        btnSave: MaterialButton
    ) {

        if (documentId.isBlank()) {

            Toast.makeText(
                this,
                "Appointment document ID not found",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        btnSave.isEnabled =
            false

        btnSave.text =
            "Saving..."

        val answers =
            hashMapOf<String, Any>(

                "feelingWell" to
                        getSelectedAnswer(
                            rgFeelingWell
                        ),

                "recentIllness" to
                        getSelectedAnswer(
                            rgRecentIllness
                        ),

                "medication" to
                        getSelectedAnswer(
                            rgMedication
                        ),

                "recentDonation" to
                        getSelectedAnswer(
                            rgRecentDonation
                        ),

                "decision" to
                        decision,

                "notes" to
                        notes,

                "screenedAt" to
                        FieldValue.serverTimestamp()
            )

        val newStatus =

            if (
                decision.equals(
                    "Eligible",
                    ignoreCase = true
                )
            ) {

                "Eligible"

            } else {

                "Deferred"
            }

        val updates =
            hashMapOf<String, Any>(

                "screening" to
                        answers,

                "screeningDecision" to
                        decision,

                "status" to
                        newStatus,

                "updatedAt" to
                        FieldValue.serverTimestamp()
            )

        firestore
            .collection(
                "donationAppointments"
            )
            .document(
                documentId
            )
            .update(
                updates
            )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Screening result saved: $decision",
                    Toast.LENGTH_SHORT
                ).show()

                if (
                    decision.equals(
                        "Eligible",
                        ignoreCase = true
                    )
                ) {

                    openRecordDonation()

                } else {

                    /*
                     * Deferred donor should NOT continue
                     * to completed donation recording.
                     */

                    finish()
                }
            }
            .addOnFailureListener { exception ->

                btnSave.isEnabled =
                    true

                btnSave.text =
                    "Save Screening Result"

                Toast.makeText(
                    this,
                    "Unable to save screening: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // OPEN RECORD DONATION
    // =========================================================

    private fun openRecordDonation() {

        val intent =
            Intent(
                this,
                StaffRecordDonationActivity::class.java
            )

        intent.putExtra(
            "DOCUMENT_ID",
            documentId
        )

        intent.putExtra(
            "APPOINTMENT_ID",
            appointmentId
        )

        intent.putExtra(
            "DONOR_NAME",
            donorName
        )

        intent.putExtra(
            "DONOR_ID",
            donorId
        )

        intent.putExtra(
            "BLOOD_GROUP",
            bloodGroup
        )

        intent.putExtra(
            "CENTER",
            center
        )

        startActivity(
            intent
        )

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        finish()
    }

    // =========================================================
    // GET SELECTED ANSWER
    // =========================================================

    private fun getSelectedAnswer(
        group: RadioGroup
    ): String {

        val selectedId =
            group.checkedRadioButtonId

        if (selectedId == -1) {
            return ""
        }

        return findViewById<RadioButton>(
            selectedId
        ).text
            .toString()
            .trim()
    }

    // =========================================================
    // ANSWER CHECK
    // =========================================================

    private fun isAnswered(
        group: RadioGroup
    ): Boolean {

        return group.checkedRadioButtonId != -1
    }

    // =========================================================
    // MESSAGE
    // =========================================================

    private fun showMessage(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}