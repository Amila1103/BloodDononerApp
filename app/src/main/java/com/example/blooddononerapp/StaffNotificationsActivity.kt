package com.example.blooddononerapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StaffNotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_notifications
        )

        val btnBack =
            findViewById<TextView>(R.id.btnBack)

        val txtMarkAllRead =
            findViewById<TextView>(R.id.txtMarkAllRead)

        btnBack.setOnClickListener {
            finish()
        }

        txtMarkAllRead.setOnClickListener {

            Toast.makeText(
                this,
                "All notifications marked as read",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}