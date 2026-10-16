
package com.example.blooddononerapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.Locale

class AdminDonationDetailsActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var txtDonationId: TextView
    private lateinit var txtBloodGroup: TextView
    private lateinit var txtDonor: TextView
    private lateinit var txtType: TextView
    private lateinit var txtAmount: TextView
    private lateinit var txtHospital: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_donation_details)

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        txtDonationId = findViewById(R.id.txtDonationId)
        txtBloodGroup = findViewById(R.id.txtBloodGroup)
        txtDonor = findViewById(R.id.txtDonor)
        txtType = findViewById(R.id.txtDonationType)
        txtAmount = findViewById(R.id.txtAmount)
        txtHospital = findViewById(R.id.txtHospital)
        txtDate = findViewById(R.id.txtDate)
        txtStatus = findViewById(R.id.txtStatus)

        listOf(
            txtDonationId, txtBloodGroup, txtDonor, txtType,
            txtAmount, txtHospital, txtDate, txtStatus
        ).forEach { it.text = "Loading..." }

        val documentId = intent.getStringExtra("DONATION_DOCUMENT_ID")
        if (documentId.isNullOrBlank()) {
            Toast.makeText(this, "Donation record ID is missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        firestore.collection("donationRecords")
            .document(documentId)
            .get(Source.SERVER)
            .addOnSuccessListener { document ->
                if (isFinishing || isDestroyed) return@addOnSuccessListener
                if (!document.exists()) {
                    Toast.makeText(this, "Donation record not found", Toast.LENGTH_LONG).show()
                    finish()
                    return@addOnSuccessListener
                }
                displayDonation(document)
            }
            .addOnFailureListener { error ->
                if (isFinishing || isDestroyed) return@addOnFailureListener
                txtDonationId.text = "Unable to load record"
                Toast.makeText(
                    this,
                    "Firebase error: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
    }

    private fun displayDonation(document: DocumentSnapshot) {
        txtDonationId.text = document.getString("donationId")
            ?.takeIf { it.isNotBlank() } ?: document.id
        txtBloodGroup.text = document.getString("bloodGroup")
            ?.takeIf { it.isNotBlank() } ?: "–"
        txtDonor.text = document.getString("donorName")
            ?.takeIf { it.isNotBlank() } ?: "Not recorded"
        txtType.text = document.getString("donationType")
            ?.takeIf { it.isNotBlank() } ?: "Not recorded"

        val amountMl = document.get("amountMl") as? Number
        txtAmount.text = if (amountMl != null) {
            "${amountMl.toInt()} mL"
        } else {
            "Not recorded"
        }

        txtHospital.text = document.getString("hospital")
            ?.takeIf { it.isNotBlank() } ?: "Not recorded"

        val savedDate = document.getString("recordedDateTime")
        txtDate.text = if (!savedDate.isNullOrBlank()) {
            savedDate
        } else {
            document.getTimestamp("createdAt")?.toDate()?.let {
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(it)
            } ?: "Not recorded"
        }

        txtStatus.text = document.getString("status") ?: "Unknown"
    }
}
