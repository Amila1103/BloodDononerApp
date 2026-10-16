package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminEmergencyRequestsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerRequests: RecyclerView

    private lateinit var filterAll: TextView
    private lateinit var filterPending: TextView
    private lateinit var filterMatching: TextView
    private lateinit var filterFulfilled: TextView

    private lateinit var adapter: AdminEmergencyRequestAdapter

    private val allRequests =
        mutableListOf<AdminEmergencyRequest>()

    private var currentFilter = "All"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_emergency_requests
        )


        // =====================================================
        // FIREBASE
        // =====================================================

        firestore =
            FirebaseFirestore.getInstance()


        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        filterAll =
            findViewById(
                R.id.filterAll
            )

        filterPending =
            findViewById(
                R.id.filterPending
            )

        filterMatching =
            findViewById(
                R.id.filterMatching
            )

        filterFulfilled =
            findViewById(
                R.id.filterFulfilled
            )

        recyclerRequests =
            findViewById(
                R.id.recyclerEmergencyRequests
            )


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerRequests.layoutManager =
            LinearLayoutManager(this)

        adapter =
            AdminEmergencyRequestAdapter(
                mutableListOf()
            ) { request ->

                openRequestDetails(request)
            }

        recyclerRequests.adapter = adapter


        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }


        // =====================================================
        // FILTERS
        // =====================================================

        filterAll.setOnClickListener {

            currentFilter = "All"

            updateFilterUI()

            applyFilter()
        }


        filterPending.setOnClickListener {

            currentFilter = "Pending"

            updateFilterUI()

            applyFilter()
        }


        filterMatching.setOnClickListener {

            currentFilter = "Matching"

            updateFilterUI()

            applyFilter()
        }


        filterFulfilled.setOnClickListener {

            currentFilter = "Fulfilled"

            updateFilterUI()

            applyFilter()
        }


        // =====================================================
        // INITIAL FILTER UI
        // =====================================================

        updateFilterUI()


        // =====================================================
        // LOAD FIRESTORE
        // =====================================================

        loadEmergencyRequests()
    }


    override fun onResume() {
        super.onResume()

        if (::firestore.isInitialized) {

            loadEmergencyRequests()
        }
    }


    // =========================================================
    // LOAD REQUESTS
    // =========================================================

    private fun loadEmergencyRequests() {

        firestore
            .collection("emergencyRequests")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()

            .addOnSuccessListener { documents ->

                allRequests.clear()

                for (document in documents) {

                    val request =
                        AdminEmergencyRequest(

                            documentId =
                                document.id,

                            requestId =
                                document.getString(
                                    "requestId"
                                ) ?: document.id,

                            userId =
                                document.getString(
                                    "userId"
                                ) ?: "",

                            userEmail =
                                document.getString(
                                    "userEmail"
                                ) ?: "",

                            bloodGroup =
                                document.getString(
                                    "bloodGroup"
                                ) ?: "",

                            units =
                                document.getString(
                                    "units"
                                ) ?: "",

                            province =
                                document.getString(
                                    "province"
                                ) ?: "",

                            district =
                                document.getString(
                                    "district"
                                ) ?: "",

                            area =
                                document.getString(
                                    "area"
                                ) ?: "",

                            hospital =
                                document.getString(
                                    "hospital"
                                ) ?: "",

                            requiredDate =
                                document.getString(
                                    "requiredDate"
                                ) ?: "",

                            requiredBefore =
                                document.getString(
                                    "requiredBefore"
                                ) ?: "",

                            primaryContact =
                                document.getString(
                                    "primaryContact"
                                ) ?: "",

                            secondaryContact =
                                document.getString(
                                    "secondaryContact"
                                ) ?: "",

                            urgency =
                                document.getString(
                                    "urgency"
                                ) ?: "",

                            additionalInfo =
                                document.getString(
                                    "additionalInfo"
                                ) ?: "",

                            status =
                                document.getString(
                                    "status"
                                ) ?: "Pending"
                        )

                    allRequests.add(request)
                }


                applyFilter()
            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load requests: ${
                        exception.message ?: "Unknown error"
                    }",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // =========================================================
    // FILTER REQUESTS
    // =========================================================

    private fun applyFilter() {

        val filteredRequests =

            if (
                currentFilter.equals(
                    "All",
                    ignoreCase = true
                )
            ) {

                allRequests

            } else {

                allRequests.filter {

                    it.status.equals(
                        currentFilter,
                        ignoreCase = true
                    )
                }
            }


        adapter.updateRequests(
            filteredRequests
        )
    }


    // =========================================================
    // FILTER DESIGN
    // =========================================================

    private fun updateFilterUI() {

        setFilterNormal(filterAll)
        setFilterNormal(filterPending)
        setFilterNormal(filterMatching)
        setFilterNormal(filterFulfilled)


        when (currentFilter) {

            "Pending" ->
                setFilterSelected(
                    filterPending
                )

            "Matching" ->
                setFilterSelected(
                    filterMatching
                )

            "Fulfilled" ->
                setFilterSelected(
                    filterFulfilled
                )

            else ->
                setFilterSelected(
                    filterAll
                )
        }
    }


    private fun setFilterSelected(
        view: TextView
    ) {

        view.setBackgroundResource(
            R.drawable.bg_staff_filter_selected
        )

        view.setTextColor(
            android.graphics.Color.WHITE
        )
    }


    private fun setFilterNormal(
        view: TextView
    ) {

        view.setBackgroundResource(
            R.drawable.bg_staff_filter
        )

        view.setTextColor(
            android.graphics.Color.parseColor(
                "#666666"
            )
        )
    }


    // =========================================================
    // OPEN DETAILS
    // =========================================================

    private fun openRequestDetails(
        request: AdminEmergencyRequest
    ) {

        val intent =
            Intent(
                this,
                AdminRequestDetailsActivity::class.java
            )

        // Only Firestore document ID is required.
        intent.putExtra(
            "REQUEST_DOCUMENT_ID",
            request.documentId
        )

        startActivity(intent)
    }
}
