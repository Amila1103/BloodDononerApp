package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminProfileActivity : AppCompatActivity() {

    private var logoutDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_profile)

        val btnBack =
            findViewById<MaterialCardView>(
                R.id.btnBack
            )

        val cardNotifications =
            findViewById<MaterialCardView>(
                R.id.cardAdminNotificationSettings
            )

        val cardHelp =
            findViewById<MaterialCardView>(
                R.id.cardAdminHelp
            )

        val btnLogout =
            findViewById<MaterialButton>(
                R.id.btnAdminLogout
            )

        // Back navigation
        btnBack.setOnClickListener {
            finish()
        }

        // Notification settings
        cardNotifications.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NotificationSettingsActivity::class.java
                )
            )
        }

        // Help and support
        cardHelp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HelpSupportActivity::class.java
                )
            )
        }

        // Enable Logout button
        btnLogout.isEnabled = true
        btnLogout.isClickable = true
        btnLogout.isFocusable = true

        // Logout button action
        btnLogout.setOnClickListener {

            Log.d(
                "AdminLogout",
                "Logout button clicked"
            )

            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {

        if (isFinishing || isDestroyed) {
            return
        }

        if (logoutDialog?.isShowing == true) {
            return
        }

        logoutDialog = AlertDialog.Builder(this)
            .setTitle("Admin Logout")
            .setMessage(
                "Are you sure you want to logout " +
                        "from your Admin account?"
            )
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->

                Log.d(
                    "AdminLogout",
                    "Admin confirmed logout"
                )

                performLogout()
            }
            .create()

        logoutDialog?.show()
    }

    private fun performLogout() {

        if (isFinishing || isDestroyed) {
            return
        }

        SessionSecurity.logout(
            this,
            "Admin logged out successfully"
        )
    }

    override fun onDestroy() {

        logoutDialog?.dismiss()
        logoutDialog = null

        super.onDestroy()
    }
}
