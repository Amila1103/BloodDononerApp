
package com.example.blooddononerapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class StaffProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_profile)

        val btnBack =
            findViewById<TextView>(R.id.btnBack)

        val cardNotificationSettings =
            findViewById<MaterialCardView>(
                R.id.cardNotificationSettings
            )

        val cardHelp =
            findViewById<MaterialCardView>(
                R.id.cardHelp
            )

        val cardLogout =
            findViewById<MaterialCardView>(
                R.id.cardLogout
            )

        btnBack.setOnClickListener {
            finish()
        }

        cardNotificationSettings.setOnClickListener {
            Toast.makeText(
                this,
                "Notification Settings",
                Toast.LENGTH_SHORT
            ).show()
        }

        cardHelp.setOnClickListener {
            Toast.makeText(
                this,
                "Help & Support",
                Toast.LENGTH_SHORT
            ).show()
        }

        cardLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage(
                    "Are you sure you want to logout?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout") { _, _ ->
                    SessionSecurity.logout(
                        this,
                        "Logged out successfully"
                    )
                }
                .show()
        }
    }
}
