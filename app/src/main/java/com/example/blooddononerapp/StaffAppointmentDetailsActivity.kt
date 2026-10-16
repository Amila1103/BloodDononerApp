package com.example.blooddononerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StaffAppointmentDetailsActivity : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var firestore: FirebaseFirestore

    // =========================================================
    // APPOINTMENT DATA
    // =========================================================

    private var documentId = ""
    private var appointmentId = ""

    private var donorName = ""
    private var donorId = ""
    private var bloodGroup = ""

    private var donationDate = ""
    private var donationTime = ""
    private var center = ""

    private var status = "Pending"
    private var checkedIn = false

    // =========================================================
    // UI
    // =========================================================

    private lateinit var btnCheckIn: MaterialButton

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_appointment_details
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        firestore =
            FirebaseFirestore.getInstance()

        // =====================================================
        // GET INTENT DATA
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

        donationDate =
            intent.getStringExtra(
                "DATE"
            ) ?: "-"

        donationTime =
            intent.getStringExtra(
                "TIME"
            ) ?: "-"

        center =
            intent.getStringExtra(
                "CENTER"
            ) ?: "-"

        status =
            intent.getStringExtra(
                "STATUS"
            ) ?: "Pending"

        checkedIn =
            intent.getBooleanExtra(
                "CHECKED_IN",
                false
            )

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        val txtAppointmentId =
            findViewById<TextView>(
                R.id.txtAppointmentId
            )

        val txtBloodGroup =
            findViewById<TextView>(
                R.id.txtBloodGroup
            )

        val txtDonorName =
            findViewById<TextView>(
                R.id.txtDonorName
            )

        val txtDonorId =
            findViewById<TextView>(
                R.id.txtDonorId
            )

        val txtStatus =
            findViewById<TextView>(
                R.id.txtStatus
            )

        val txtAppointmentDate =
            findViewById<TextView>(
                R.id.txtAppointmentDate
            )

        val txtAppointmentTime =
            findViewById<TextView>(
                R.id.txtAppointmentTime
            )

        val txtCenter =
            findViewById<TextView>(
                R.id.txtCenter
            )

        btnCheckIn =
            findViewById(
                R.id.btnCheckIn
            )

        // =====================================================
        // SET REAL APPOINTMENT DATA
        // =====================================================

        txtAppointmentId.text =
            appointmentId

        txtBloodGroup.text =
            bloodGroup

        txtDonorName.text =
            donorName

        txtDonorId.text =
            donorId

        txtStatus.text =
            if (checkedIn) {
                "Checked In"
            } else {
                status
            }

        txtAppointmentDate.text =
            donationDate

        txtAppointmentTime.text =
            donationTime

        txtCenter.text =
            center

        // =====================================================
        // BUTTON STATE
        // =====================================================

        updateButtonState()

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()

            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }

        // =====================================================
        // CHECK IN
        // =====================================================

        btnCheckIn.setOnClickListener {

            when {

                status.equals(
                    "Completed",
                    ignoreCase = true
                ) -> {

                    Toast.makeText(
                        this,
                        "This donation is already completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                status.equals(
                    "Cancelled",
                    ignoreCase = true
                ) ||
                        status.equals(
                            "Rejected",
                            ignoreCase = true
                        ) -> {

                    Toast.makeText(
                        this,
                        "This appointment is not active",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                checkedIn ||
                        status.equals(
                            "Checked In",
                            ignoreCase = true
                        ) -> {

                    // Already checked in.
                    // Continue screening without writing again.

                    openScreening()
                }

                else -> {

                    showCheckInConfirmation()
                }
            }
        }
    }

    // =========================================================
    // BUTTON STATE
    // =========================================================

    private fun updateButtonState() {

        when {

            status.equals(
                "Completed",
                ignoreCase = true
            ) -> {

                btnCheckIn.isEnabled =
                    false

                btnCheckIn.text =
                    "Donation Completed"
            }

            status.equals(
                "Cancelled",
                ignoreCase = true
            ) ||
                    status.equals(
                        "Rejected",
                        ignoreCase = true
                    ) -> {

                btnCheckIn.isEnabled =
                    false

                btnCheckIn.text =
                    "Appointment Cancelled"
            }

            checkedIn ||
                    status.equals(
                        "Checked In",
                        ignoreCase = true
                    ) -> {

                btnCheckIn.isEnabled =
                    true

                btnCheckIn.text =
                    "Continue Screening"
            }

            else -> {

                btnCheckIn.isEnabled =
                    true

                btnCheckIn.text =
                    "Check In & Start Screening"
            }
        }
    }

    // =========================================================
    // CONFIRM CHECK IN
    // =========================================================

    private fun showCheckInConfirmation() {

        AlertDialog.Builder(this)
            .setTitle(
                "Confirm Donor Check-in"
            )
            .setMessage(
                "Confirm that $donorName has arrived for the donation appointment."
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Check In"
            ) { _, _ ->

                checkInDonor()
            }
            .show()
    }

    // =========================================================
    // FIREBASE CHECK IN
    // =========================================================

    private fun checkInDonor() {

        if (documentId.isBlank()) {

            Toast.makeText(
                this,
                "Appointment document ID not found",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Prevent multiple clicks while Firebase is updating.

        btnCheckIn.isEnabled =
            false

        btnCheckIn.text =
            "Checking In..."

        val updates =
            hashMapOf<String, Any>(

                "checkedIn" to true,

                "status" to "Checked In",

                "checkedInAt" to
                        FieldValue.serverTimestamp(),

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

                checkedIn = true
                status = "Checked In"

                Toast.makeText(
                    this,
                    "Donor checked in successfully",
                    Toast.LENGTH_SHORT
                ).show()

                openScreening()
            }
            .addOnFailureListener { exception ->

                btnCheckIn.isEnabled =
                    true

                btnCheckIn.text =
                    "Check In & Start Screening"

                Toast.makeText(
                    this,
                    "Check-in failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // OPEN SCREENING
    // =========================================================

    private fun openScreening() {

        val screeningIntent =
            Intent(
                this,
                StaffDonorScreeningActivity::class.java
            )

        // Actual Firestore document ID.
        // We will need this when saving screening results.

        screeningIntent.putExtra(
            "DOCUMENT_ID",
            documentId
        )

        screeningIntent.putExtra(
            "APPOINTMENT_ID",
            appointmentId
        )

        screeningIntent.putExtra(
            "DONOR_NAME",
            donorName
        )

        screeningIntent.putExtra(
            "DONOR_ID",
            donorId
        )

        screeningIntent.putExtra(
            "BLOOD_GROUP",
            bloodGroup
        )

        screeningIntent.putExtra(
            "DATE",
            donationDate
        )

        screeningIntent.putExtra(
            "TIME",
            donationTime
        )

        screeningIntent.putExtra(
            "CENTER",
            center
        )

        startActivity(
            screeningIntent
        )

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}