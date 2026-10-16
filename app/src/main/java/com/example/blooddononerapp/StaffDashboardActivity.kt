package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class StaffDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_dashboard
        )

        // =====================================================
        // HEADER
        // =====================================================

        val txtGreeting =
            findViewById<TextView>(
                R.id.txtGreeting
            )

        val btnStaffNotifications =
            findViewById<MaterialCardView>(
                R.id.btnStaffNotifications
            )

        val btnStaffProfile =
            findViewById<MaterialCardView>(
                R.id.btnStaffProfile
            )


        // =====================================================
        // MAIN CARDS
        // =====================================================

        val cardEmergencyRequests =
            findViewById<MaterialCardView>(
                R.id.cardEmergencyRequests
            )

        val cardStaffAppointments =
            findViewById<MaterialCardView>(
                R.id.cardStaffAppointments
            )

        val cardStaffInventory =
            findViewById<MaterialCardView>(
                R.id.cardStaffInventory
            )

        val cardDonorRecords =
            findViewById<MaterialCardView>(
                R.id.cardDonorRecords
            )

        val cardRecordDonation =
            findViewById<MaterialCardView>(
                R.id.cardRecordDonation
            )

        val cardStockSummary =
            findViewById<MaterialCardView>(
                R.id.cardStockSummary
            )

        val txtViewAllStock =
            findViewById<TextView>(
                R.id.txtViewAllStock
            )

        val cardRecentAlert =
            findViewById<MaterialCardView>(
                R.id.cardRecentAlert
            )


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        val navStaffDashboard =
            findViewById<LinearLayout>(
                R.id.navStaffDashboard
            )

        val navStaffRequests =
            findViewById<LinearLayout>(
                R.id.navStaffRequests
            )

        val navStaffAppointments =
            findViewById<LinearLayout>(
                R.id.navStaffAppointments
            )

        val navStaffInventory =
            findViewById<LinearLayout>(
                R.id.navStaffInventory
            )

        val navStaffProfile =
            findViewById<LinearLayout>(
                R.id.navStaffProfile
            )


        // =====================================================
        // GREETING
        // =====================================================

        setGreeting(
            txtGreeting
        )


        // =====================================================
        // EMERGENCY REQUESTS
        // =====================================================

        cardEmergencyRequests.setOnClickListener {

            openScreen(
                StaffEmergencyRequestsActivity::class.java
            )
        }


        // =====================================================
        // APPOINTMENTS
        // =====================================================

        cardStaffAppointments.setOnClickListener {

            openScreen(
                StaffAppointmentsActivity::class.java
            )
        }


        // =====================================================
        // BLOOD INVENTORY
        // =====================================================

        cardStaffInventory.setOnClickListener {

            openScreen(
                StaffBloodInventoryActivity::class.java
            )
        }


        cardStockSummary.setOnClickListener {

            openScreen(
                StaffBloodInventoryActivity::class.java
            )
        }


        txtViewAllStock.setOnClickListener {

            openScreen(
                StaffBloodInventoryActivity::class.java
            )
        }


        cardRecentAlert.setOnClickListener {

            openScreen(
                StaffBloodInventoryActivity::class.java
            )
        }


        // =====================================================
        // DONOR RECORDS
        // =====================================================

        cardDonorRecords.setOnClickListener {

            openScreen(
                StaffDonorRecordsActivity::class.java
            )
        }


        // =====================================================
        // RECORD DONATION
        // =====================================================

        cardRecordDonation.setOnClickListener {

            openScreen(
                StaffRecordDonationActivity::class.java
            )
        }


        // =====================================================
        // NOTIFICATIONS
        // =====================================================

        btnStaffNotifications.setOnClickListener {

            openScreen(
                StaffNotificationsActivity::class.java
            )
        }


        // =====================================================
        // PROFILE
        // =====================================================

        btnStaffProfile.setOnClickListener {

            openScreen(
                StaffProfileActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM - DASHBOARD
        // =====================================================

        navStaffDashboard.setOnClickListener {

            // Already here
        }


        // =====================================================
        // BOTTOM - REQUESTS
        // =====================================================

        navStaffRequests.setOnClickListener {

            openScreen(
                StaffEmergencyRequestsActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM - APPOINTMENTS
        // =====================================================

        navStaffAppointments.setOnClickListener {

            openScreen(
                StaffAppointmentsActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM - INVENTORY
        // =====================================================

        navStaffInventory.setOnClickListener {

            openScreen(
                StaffBloodInventoryActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM - PROFILE
        // =====================================================

        navStaffProfile.setOnClickListener {

            openScreen(
                StaffProfileActivity::class.java
            )
        }
    }


    // =========================================================
    // OPEN SCREEN
    // =========================================================

    private fun openScreen(
        activity: Class<*>
    ) {

        val intent =
            Intent(
                this,
                activity
            )

        startActivity(
            intent
        )

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }


    // =========================================================
    // GREETING
    // =========================================================

    private fun setGreeting(
        textView: TextView
    ) {

        val hour =
            Calendar
                .getInstance()
                .get(
                    Calendar.HOUR_OF_DAY
                )

        val greeting =
            when (hour) {

                in 5..11 ->
                    "Good Morning,"

                in 12..16 ->
                    "Good Afternoon,"

                else ->
                    "Good Evening,"
            }

        textView.text =
            greeting
    }
}