package com.example.blooddononerapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reports)

        val btnBack =
            findViewById<MaterialCardView>(R.id.btnBack)

        val btnGenerateReport =
            findViewById<MaterialButton>(R.id.btnGenerateReport)

        val cardUsers =
            findViewById<MaterialCardView>(R.id.cardReportUsers)

        val cardDonations =
            findViewById<MaterialCardView>(R.id.cardReportDonations)

        val cardRequests =
            findViewById<MaterialCardView>(R.id.cardReportRequests)

        val cardHospitals =
            findViewById<MaterialCardView>(R.id.cardReportHospitals)


        btnBack.setOnClickListener {
            finish()
        }


        cardUsers.setOnClickListener {
            showMessage("User report selected")
        }

        cardDonations.setOnClickListener {
            showMessage("Donation report selected")
        }

        cardRequests.setOnClickListener {
            showMessage("Emergency request report selected")
        }

        cardHospitals.setOnClickListener {
            showMessage("Hospital report selected")
        }


        btnGenerateReport.setOnClickListener {

            showMessage(
                "Report generation will be connected to backend data"
            )
        }
    }


    private fun showMessage(message: String) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}