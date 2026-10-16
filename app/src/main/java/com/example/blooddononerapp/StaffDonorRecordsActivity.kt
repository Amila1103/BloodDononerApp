package com.example.blooddononerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StaffDonorRecordsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var recordsContainer: LinearLayout
    private lateinit var txtEmptyRecords: TextView
    private lateinit var txtRecordSummary: TextView
    private lateinit var edtSearch: EditText

    private var staffHospital = ""

    private val allRecords =
        mutableListOf<DonorRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_donor_records
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        auth =
            FirebaseAuth.getInstance()

        firestore =
            FirebaseFirestore.getInstance()

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        edtSearch =
            findViewById(
                R.id.edtSearchDonor
            )

        recordsContainer =
            findViewById(
                R.id.recordsContainer
            )

        txtEmptyRecords =
            findViewById(
                R.id.txtEmptyRecords
            )

        txtRecordSummary =
            findViewById(
                R.id.txtRecordSummary
            )

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearch.addTextChangedListener {

            val query =
                it?.toString()
                    ?.trim()
                    ?.lowercase()
                    .orEmpty()

            filterRecords(
                query
            )
        }

        // =====================================================
        // START
        // =====================================================

        loadStaffHospital()
    }

    // =========================================================
    // LOAD LOGGED-IN STAFF HOSPITAL
    // =========================================================

    private fun loadStaffHospital() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Staff account not found",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        txtRecordSummary.text =
            "Loading donor records..."

        firestore
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Staff profile not found",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val role =
                    document.getString("role")
                        .orEmpty()

                if (
                    !role.equals(
                        "STAFF",
                        ignoreCase = true
                    )
                ) {

                    Toast.makeText(
                        this,
                        "This account is not a staff account",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                staffHospital =
                    document.getString("hospital")
                        ?.trim()
                        .orEmpty()

                if (
                    staffHospital.isBlank() ||
                    staffHospital.equals(
                        "Not Assigned",
                        ignoreCase = true
                    )
                ) {

                    txtRecordSummary.text =
                        "No hospital assigned"

                    Toast.makeText(
                        this,
                        "No hospital assigned to this staff account",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                loadDonationRecords()
            }
            .addOnFailureListener { exception ->

                txtRecordSummary.text =
                    "Unable to load records"

                Toast.makeText(
                    this,
                    "Failed to load staff hospital: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // LOAD DONATION RECORDS
    // =========================================================

    private fun loadDonationRecords() {

        txtRecordSummary.text =
            "Loading $staffHospital donor records..."

        firestore
            .collection("donationRecords")
            .whereEqualTo(
                "hospital",
                staffHospital
            )
            .get()
            .addOnSuccessListener { result ->

                allRecords.clear()

                for (document in result.documents) {

                    val record =
                        DonorRecord(
                            documentId =
                                document.id,

                            donationId =
                                document.getString(
                                    "donationId"
                                ).orEmpty(),

                            donorId =
                                document.getString(
                                    "donorId"
                                ).orEmpty(),

                            donorName =
                                document.getString(
                                    "donorName"
                                ).orEmpty(),

                            bloodGroup =
                                document.getString(
                                    "bloodGroup"
                                ).orEmpty(),

                            donationType =
                                document.getString(
                                    "donationType"
                                ).orEmpty(),

                            amountMl =
                                (
                                        document.getLong(
                                            "amountMl"
                                        ) ?: 0L
                                        ).toInt(),

                            hospital =
                                document.getString(
                                    "hospital"
                                ).orEmpty(),

                            recordedDateTime =
                                document.getString(
                                    "recordedDateTime"
                                ).orEmpty(),

                            status =
                                document.getString(
                                    "status"
                                ) ?: "Completed"
                        )

                    allRecords.add(
                        record
                    )
                }

                /*
                 * Newer donation IDs normally appear first.
                 * We avoid Firestore orderBy here so that
                 * a composite index is not required.
                 */
                allRecords.reverse()

                showRecords(
                    allRecords
                )
            }
            .addOnFailureListener { exception ->

                txtRecordSummary.text =
                    "Unable to load records"

                Toast.makeText(
                    this,
                    "Failed to load donor records: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // SEARCH / FILTER
    // =========================================================

    private fun filterRecords(
        query: String
    ) {

        if (query.isBlank()) {

            showRecords(
                allRecords
            )

            return
        }

        val filtered =
            allRecords.filter { record ->

                record.donorName
                    .lowercase()
                    .contains(query) ||

                        record.donorId
                            .lowercase()
                            .contains(query) ||

                        record.bloodGroup
                            .lowercase()
                            .contains(query) ||

                        record.donationId
                            .lowercase()
                            .contains(query)
            }

        showRecords(
            filtered
        )
    }

    // =========================================================
    // DISPLAY RECORDS
    // =========================================================

    private fun showRecords(
        records: List<DonorRecord>
    ) {

        recordsContainer.removeAllViews()

        if (records.isEmpty()) {

            txtEmptyRecords.visibility =
                View.VISIBLE

            txtRecordSummary.text =
                if (staffHospital.isBlank()) {
                    "No donor records"
                } else {
                    "0 records • $staffHospital"
                }

            return
        }

        txtEmptyRecords.visibility =
            View.GONE

        txtRecordSummary.text =
            "${records.size} record(s) • $staffHospital"

        for (record in records) {

            addRecordCard(
                record
            )
        }
    }

    // =========================================================
    // ADD DYNAMIC RECORD CARD
    // =========================================================

    private fun addRecordCard(
        record: DonorRecord
    ) {

        val view =
            LayoutInflater.from(this)
                .inflate(
                    R.layout.item_staff_donor_record,
                    recordsContainer,
                    false
                )

        val txtBloodGroup =
            view.findViewById<TextView>(
                R.id.txtBloodGroup
            )

        val txtDonorName =
            view.findViewById<TextView>(
                R.id.txtDonorName
            )

        val txtDonorId =
            view.findViewById<TextView>(
                R.id.txtDonorId
            )

        val txtDonationInfo =
            view.findViewById<TextView>(
                R.id.txtDonationInfo
            )

        val txtDateTime =
            view.findViewById<TextView>(
                R.id.txtDateTime
            )

        val txtStatus =
            view.findViewById<TextView>(
                R.id.txtStatus
            )

        // =====================================================
        // SET DATA
        // =====================================================

        txtBloodGroup.text =
            record.bloodGroup.ifBlank {
                "--"
            }

        txtDonorName.text =
            record.donorName.ifBlank {
                "Unknown Donor"
            }

        txtDonorId.text =
            "Donor ID: ${
                record.donorId.ifBlank {
                    "--"
                }
            }"

        val type =
            record.donationType.ifBlank {
                "Donation"
            }

        txtDonationInfo.text =
            "$type • ${record.amountMl} mL"

        txtDateTime.text =
            if (record.recordedDateTime.isBlank()) {
                record.donationId
            } else {
                record.recordedDateTime
            }

        txtStatus.text =
            "Status: ${
                record.status.ifBlank {
                    "Completed"
                }
            }"

        recordsContainer.addView(
            view
        )
    }

    // =========================================================
    // DATA CLASS
    // =========================================================

    data class DonorRecord(

        val documentId: String,

        val donationId: String,

        val donorId: String,

        val donorName: String,

        val bloodGroup: String,

        val donationType: String,

        val amountMl: Int,

        val hospital: String,

        val recordedDateTime: String,

        val status: String
    )
}