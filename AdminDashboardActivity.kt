package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_dashboard
        )


        // =====================================================
        // HEADER
        // =====================================================

        val txtGreeting =
            findViewById<TextView>(
                R.id.txtAdminGreeting
            )

        val btnNotifications =
            findViewById<MaterialCardView>(
                R.id.btnAdminNotifications
            )

        val btnProfile =
            findViewById<MaterialCardView>(
                R.id.btnAdminProfile
            )


        // =====================================================
        // MAIN CARDS
        // =====================================================

        val cardUsers =
            findViewById<MaterialCardView>(
                R.id.cardAdminUsers
            )

        val cardStaff =
            findViewById<MaterialCardView>(
                R.id.cardAdminStaff
            )

        val cardHospitals =
            findViewById<MaterialCardView>(
                R.id.cardAdminHospitals
            )

        val cardRequests =
            findViewById<MaterialCardView>(
                R.id.cardAdminRequests
            )

        val cardDonations =
            findViewById<MaterialCardView>(
                R.id.cardAdminDonations
            )

        val cardInventory =
            findViewById<MaterialCardView>(
                R.id.cardAdminInventory
            )

        val cardReports =
            findViewById<MaterialCardView>(
                R.id.cardAdminReports
            )


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        val navDashboard =
            findViewById<LinearLayout>(
                R.id.navAdminDashboard
            )

        val navUsers =
            findViewById<LinearLayout>(
                R.id.navAdminUsers
            )

        val navHospitals =
            findViewById<LinearLayout>(
                R.id.navAdminHospitals
            )

        val navReports =
            findViewById<LinearLayout>(
                R.id.navAdminReports
            )

        val navProfile =
            findViewById<LinearLayout>(
                R.id.navAdminProfile
            )


        // =====================================================
        // GREETING
        // =====================================================

        setGreeting(
            txtGreeting
        )


        // =====================================================
        // USERS
        // =====================================================

        cardUsers.setOnClickListener {

            openScreen(
                AdminUserManagementActivity::class.java
            )
        }


        // =====================================================
        // STAFF
        // =====================================================

        cardStaff.setOnClickListener {

            openScreen(
                AdminStaffManagementActivity::class.java
            )
        }


        // =====================================================
        // HOSPITALS
        // =====================================================

        cardHospitals.setOnClickListener {

            openScreen(
                AdminHospitalManagementActivity::class.java
            )
        }


        // =====================================================
        // EMERGENCY REQUESTS
        // =====================================================

        cardRequests.setOnClickListener {

            openScreen(
                AdminEmergencyRequestsActivity::class.java
            )
        }


        // =====================================================
        // DONATIONS
        // =====================================================

        cardDonations.setOnClickListener {

            openScreen(
                AdminDonationManagementActivity::class.java
            )
        }


        // =====================================================
        // BLOOD INVENTORY
        // =====================================================

        cardInventory.setOnClickListener {

            openScreen(
                AdminInventoryActivity::class.java
            )
        }


        // =====================================================
        // REPORTS & ANALYTICS
        // =====================================================

        cardReports.setOnClickListener {

            openScreen(
                AdminReportsActivity::class.java
            )
        }


        // =====================================================
        // ADMIN NOTIFICATIONS
        // =====================================================

        btnNotifications.setOnClickListener {

            openScreen(
                AdminNotificationsActivity::class.java
            )
        }


        // =====================================================
        // ADMIN PROFILE
        // =====================================================

        btnProfile.setOnClickListener {

            openScreen(
                AdminProfileActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM NAV - DASHBOARD
        // =====================================================

        navDashboard.setOnClickListener {

            // Already on Admin Dashboard
        }


        // =====================================================
        // BOTTOM NAV - USERS
        // =====================================================

        navUsers.setOnClickListener {

            openScreen(
                AdminUserManagementActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM NAV - HOSPITALS
        // =====================================================

        navHospitals.setOnClickListener {

            openScreen(
                AdminHospitalManagementActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM NAV - REPORTS
        // =====================================================

        navReports.setOnClickListener {

            openScreen(
                AdminReportsActivity::class.java
            )
        }


        // =====================================================
        // BOTTOM NAV - PROFILE
        // =====================================================

        navProfile.setOnClickListener {

            openScreen(
                AdminProfileActivity::class.java
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