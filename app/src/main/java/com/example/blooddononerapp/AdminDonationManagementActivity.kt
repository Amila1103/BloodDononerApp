package com.example.blooddononerapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

class AdminDonationManagementActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val records = mutableListOf<DocumentSnapshot>()
    private var donationListener: ListenerRegistration? = null

    private lateinit var edtSearch: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var txtCount: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var recordsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_donation_management)

        edtSearch = findViewById(R.id.edtSearchDonation)
        spinnerStatus = findViewById(R.id.spinnerDonationStatus)
        txtCount = findViewById(R.id.txtDonationCount)
        txtEmpty = findViewById(R.id.txtEmptyDonations)
        recordsContainer = findViewById(R.id.layoutDonationList)

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val filters = listOf(
            "All Statuses", "Completed", "Pending", "Cancelled", "Rejected"
        )

        spinnerStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filters
        )

        spinnerStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    renderRecords()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        edtSearch.addTextChangedListener {
            renderRecords()
        }
    }

    override fun onStart() {
        super.onStart()
        txtCount.text = "Loading donations..."
        txtEmpty.text = "Loading donation records..."
        txtEmpty.visibility = View.VISIBLE

        donationListener?.remove()
        donationListener = firestore.collection("donationRecords")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    records.clear()
                    recordsContainer.removeAllViews()
                    txtCount.text = "Unable to load donations"
                    txtEmpty.text = "Firebase error: ${error.localizedMessage}"
                    txtEmpty.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                records.clear()
                records.addAll(snapshot.documents)
                records.sortWith(
                    compareByDescending<DocumentSnapshot> {
                        it.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    }
                )
                renderRecords()
            }
    }

    override fun onStop() {
        donationListener?.remove()
        donationListener = null
        super.onStop()
    }

    private fun renderRecords() {
        if (!::recordsContainer.isInitialized) return

        val query = edtSearch.text.toString().trim()
        val statusFilter = spinnerStatus.selectedItem?.toString()
            ?: "All Statuses"

        val visibleRecords = records.filter { document ->
            val status = document.getString("status") ?: "Unknown"
            val matchesStatus = statusFilter == "All Statuses" ||
                    status.equals(statusFilter, ignoreCase = true)

            val searchable = listOf(
                document.getString("donationId") ?: document.id,
                document.getString("donorName") ?: "",
                document.getString("bloodGroup") ?: "",
                document.getString("hospital") ?: "",
                status
            ).joinToString(" ")

            matchesStatus && searchable.contains(query, ignoreCase = true)
        }

        txtCount.text = "${records.size} Donation Records"
        recordsContainer.removeAllViews()

        txtEmpty.text = if (records.isEmpty()) {
            "No donation records yet. Records appear after Staff completes a donation."
        } else {
            "No donations match your search or status filter."
        }
        txtEmpty.visibility = if (visibleRecords.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        visibleRecords.forEach { document ->
            recordsContainer.addView(createDonationCard(document))
        }
    }

    private fun createDonationCard(document: DocumentSnapshot): MaterialCardView {
        val donationId = document.getString("donationId")
            ?.takeIf { it.isNotBlank() } ?: document.id
        val donor = document.getString("donorName")
            ?.takeIf { it.isNotBlank() } ?: "Unknown donor"
        val bloodGroup = document.getString("bloodGroup") ?: "?"
        val hospital = document.getString("hospital") ?: "Hospital not recorded"
        val status = document.getString("status") ?: "Unknown"
        val date = displayDate(document)

        val card = MaterialCardView(this).apply {
            radius = dp(17).toFloat()
            cardElevation = 0f
            strokeWidth = dp(1)
            strokeColor = Color.parseColor("#EAEAEA")
            setCardBackgroundColor(Color.WHITE)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                startActivity(
                    Intent(
                        this@AdminDonationManagementActivity,
                        AdminDonationDetailsActivity::class.java
                    ).putExtra("DONATION_DOCUMENT_ID", document.id)
                )
            }
        }

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(12) }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val bloodCircle = FrameLayout(this).apply {
            setBackgroundResource(R.drawable.bg_staff_blood_circle)
            layoutParams = LinearLayout.LayoutParams(dp(52), dp(52))
        }
        bloodCircle.addView(TextView(this).apply {
            text = bloodGroup
            gravity = Gravity.CENTER
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }, FrameLayout.LayoutParams(-1, -1))

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f).apply {
                marginStart = dp(14)
            }
        }

        val title = TextView(this).apply {
            text = "$donationId • $donor"
            textSize = 13f
            setTextColor(Color.parseColor("#222222"))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val location = TextView(this).apply {
            text = hospital
            textSize = 11f
            setTextColor(Color.parseColor("#777777"))
            setPadding(0, dp(5), 0, dp(4))
        }
        val state = TextView(this).apply {
            text = "$date • $status"
            textSize = 11f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(
                if (status.equals("Completed", ignoreCase = true)) "#15803D"
                else "#B45309"
            ))
        }

        content.addView(title)
        content.addView(location)
        content.addView(state)
        row.addView(bloodCircle)
        row.addView(content)
        card.addView(row)
        return card
    }

    private fun displayDate(document: DocumentSnapshot): String {
        val recordedDate = document.getString("recordedDateTime")
        if (!recordedDate.isNullOrBlank()) return recordedDate

        val createdAt = document.getTimestamp("createdAt")?.toDate()
            ?: return "Date not recorded"
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(createdAt)
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
