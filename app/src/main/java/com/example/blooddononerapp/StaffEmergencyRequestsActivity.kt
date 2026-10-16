package com.example.blooddononerapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StaffEmergencyRequestsActivity : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // =========================================================
    // UI
    // =========================================================

    private lateinit var recyclerRequests: RecyclerView
    private lateinit var txtRequestSummary: TextView
    private lateinit var txtEmptyRequests: TextView

    private lateinit var filterAll: TextView
    private lateinit var filterPending: TextView
    private lateinit var filterVerified: TextView
    private lateinit var filterMatching: TextView
    private lateinit var filterFulfilled: TextView
    private lateinit var filterRejected: TextView

    // =========================================================
    // ADAPTER / DATA
    // =========================================================

    private lateinit var adapter: StaffEmergencyRequestAdapter

    private val allRequests =
        mutableListOf<StaffEmergencyRequest>()

    private var currentFilter = "All"

    private var staffHospital = ""

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_emergency_requests
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
            findViewById<MaterialCardView>(
                R.id.btnBack
            )

        val btnRefresh =
            findViewById<MaterialCardView>(
                R.id.btnRefresh
            )

        txtRequestSummary =
            findViewById(
                R.id.txtRequestSummary
            )

        txtEmptyRequests =
            findViewById(
                R.id.txtEmptyRequests
            )

        recyclerRequests =
            findViewById(
                R.id.recyclerStaffEmergencyRequests
            )

        filterAll =
            findViewById(
                R.id.filterAll
            )

        filterPending =
            findViewById(
                R.id.filterPending
            )

        filterVerified =
            findViewById(
                R.id.filterVerified
            )

        filterMatching =
            findViewById(
                R.id.filterMatching
            )

        filterFulfilled =
            findViewById(
                R.id.filterFulfilled
            )

        filterRejected =
            findViewById(
                R.id.filterRejected
            )

        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        adapter =
            StaffEmergencyRequestAdapter(
                mutableListOf()
            )

        recyclerRequests.layoutManager =
            LinearLayoutManager(this)

        recyclerRequests.adapter =
            adapter

        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }

        // =====================================================
        // REFRESH BUTTON
        // =====================================================

        btnRefresh.setOnClickListener {

            loadStaffHospital()

            Toast.makeText(
                this,
                "Requests refreshed",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================================
        // ALL FILTER
        // =====================================================

        filterAll.setOnClickListener {

            currentFilter =
                "All"

            selectFilter(
                filterAll
            )

            applyFilter()
        }

        // =====================================================
        // PENDING FILTER
        // =====================================================

        filterPending.setOnClickListener {

            currentFilter =
                "Pending"

            selectFilter(
                filterPending
            )

            applyFilter()
        }

        // =====================================================
        // VERIFIED FILTER
        // =====================================================

        filterVerified.setOnClickListener {

            currentFilter =
                "Verified"

            selectFilter(
                filterVerified
            )

            applyFilter()
        }

        // =====================================================
        // MATCHING FILTER
        // =====================================================

        filterMatching.setOnClickListener {

            currentFilter =
                "Matching"

            selectFilter(
                filterMatching
            )

            applyFilter()
        }

        // =====================================================
        // FULFILLED FILTER
        // =====================================================

        filterFulfilled.setOnClickListener {

            currentFilter =
                "Fulfilled"

            selectFilter(
                filterFulfilled
            )

            applyFilter()
        }

        // =====================================================
        // REJECTED FILTER
        // =====================================================

        filterRejected.setOnClickListener {

            currentFilter =
                "Rejected"

            selectFilter(
                filterRejected
            )

            applyFilter()
        }

        // =====================================================
        // DEFAULT FILTER
        // =====================================================

        selectFilter(
            filterAll
        )
    }

    // =========================================================
    // ON RESUME
    // =========================================================

    override fun onResume() {
        super.onResume()

        /*
         * Screen එක open වෙනකොටත්,
         * Request Details screen එකෙන් ආපහු එනකොටත්
         * latest Firebase data reload වෙනවා.
         */

        if (
            ::firestore.isInitialized &&
            ::auth.isInitialized
        ) {

            loadStaffHospital()
        }
    }

    // =========================================================
    // GET LOGGED-IN STAFF HOSPITAL
    // =========================================================

    private fun loadStaffHospital() {

        val currentUser =
            auth.currentUser

        // =====================================================
        // CHECK LOGIN
        // =====================================================

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Staff account not found",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        txtRequestSummary.text =
            "Loading hospital requests..."

        // =====================================================
        // LOAD STAFF PROFILE
        // =====================================================

        firestore
            .collection(
                "users"
            )
            .document(
                currentUser.uid
            )
            .get()
            .addOnSuccessListener { document ->

                // =============================================
                // PROFILE EXISTS?
                // =============================================

                if (!document.exists()) {

                    showNoHospital(
                        "Staff profile not found"
                    )

                    return@addOnSuccessListener
                }

                // =============================================
                // GET ROLE
                // =============================================

                val role =
                    document.getString(
                        "role"
                    ) ?: ""

                // =============================================
                // CHECK STAFF ROLE
                // =============================================

                if (!role.equals(
                        "STAFF",
                        ignoreCase = true
                    )
                ) {

                    showNoHospital(
                        "This account is not a staff account"
                    )

                    return@addOnSuccessListener
                }

                // =============================================
                // GET STAFF HOSPITAL
                // =============================================

                staffHospital =
                    document
                        .getString(
                            "hospital"
                        )
                        ?.trim()
                        ?: ""

                // =============================================
                // CHECK HOSPITAL ASSIGNMENT
                // =============================================

                if (
                    staffHospital.isBlank() ||
                    staffHospital.equals(
                        "Not Assigned",
                        ignoreCase = true
                    )
                ) {

                    showNoHospital(
                        "No hospital assigned to this staff account"
                    )

                    return@addOnSuccessListener
                }

                // =============================================
                // LOAD HOSPITAL REQUESTS
                // =============================================

                loadHospitalRequests()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load staff profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()

                showNoHospital(
                    "Unable to load staff hospital"
                )
            }
    }

    // =========================================================
    // LOAD ONLY STAFF HOSPITAL EMERGENCY REQUESTS
    // =========================================================

    private fun loadHospitalRequests() {

        txtRequestSummary.text =
            "Loading requests for $staffHospital..."

        /*
         * IMPORTANT:
         *
         * Logged-in STAFF ගේ hospital එකට අදාළ
         * emergency requests විතරයි Firebase එකෙන් ගන්නේ.
         *
         * Example:
         *
         * Staff Hospital:
         * Divisional Hospital - Akurana
         *
         * Then only:
         * hospital == "Divisional Hospital - Akurana"
         *
         * requests load වෙනවා.
         */

        firestore
            .collection(
                "emergencyRequests"
            )
            .whereEqualTo(
                "hospital",
                staffHospital
            )
            .get()
            .addOnSuccessListener { documents ->

                allRequests.clear()

                // =============================================
                // READ REQUESTS
                // =============================================

                for (document in documents) {

                    // =========================================
                    // SAFE UNITS
                    // =========================================
                    //
                    // Firestore:
                    //
                    // units = 2
                    //
                    // OR
                    //
                    // units = "2"
                    //
                    // OR
                    //
                    // units = "2 Units"
                    //
                    // formats තුනම support කරනවා.
                    // =========================================

                    val unitsText =
                        when (
                            val value =
                                document.get(
                                    "units"
                                )
                        ) {

                            is Number -> {

                                "${value.toLong()} Units"
                            }

                            is String -> {

                                val cleanValue =
                                    value.trim()

                                if (
                                    cleanValue.contains(
                                        "unit",
                                        ignoreCase = true
                                    )
                                ) {

                                    cleanValue

                                } else {

                                    "$cleanValue Units"
                                }
                            }

                            else -> {

                                "--"
                            }
                        }

                    // =========================================
                    // CREATE REQUEST OBJECT
                    // =========================================

                    val request =
                        StaffEmergencyRequest(

                            documentId =
                                document.id,

                            requestId =
                                document.getString(
                                    "requestId"
                                )
                                    ?: document.id,

                            userId =
                                document.getString(
                                    "userId"
                                )
                                    ?: "",

                            userEmail =
                                document.getString(
                                    "userEmail"
                                )
                                    ?: "Not available",

                            bloodGroup =
                                document.getString(
                                    "bloodGroup"
                                )
                                    ?: "--",

                            units =
                                unitsText,

                            province =
                                document.getString(
                                    "province"
                                )
                                    ?: "",

                            district =
                                document.getString(
                                    "district"
                                )
                                    ?: "",

                            area =
                                document.getString(
                                    "area"
                                )
                                    ?: "",

                            hospital =
                                document.getString(
                                    "hospital"
                                )
                                    ?: "",

                            requiredDate =
                                document.getString(
                                    "requiredDate"
                                )
                                    ?: "Not available",

                            requiredBefore =
                                document.getString(
                                    "requiredBefore"
                                )
                                    ?: "Not available",

                            primaryContact =
                                document.getString(
                                    "primaryContact"
                                )
                                    ?: "Not available",

                            secondaryContact =
                                document.getString(
                                    "secondaryContact"
                                )
                                    ?: "",

                            urgency =
                                document.getString(
                                    "urgency"
                                )
                                    ?: "Not available",

                            additionalInfo =
                                document.getString(
                                    "additionalInfo"
                                )
                                    ?: "",

                            status =
                                document.getString(
                                    "status"
                                )
                                    ?: "Pending"
                        )

                    // =========================================
                    // ADD TO LIST
                    // =========================================

                    allRequests.add(
                        request
                    )
                }

                // =============================================
                // APPLY CURRENT FILTER
                // =============================================

                applyFilter()
            }
            .addOnFailureListener { exception ->

                allRequests.clear()

                adapter.updateRequests(
                    emptyList()
                )

                txtEmptyRequests.text =
                    "Unable to load requests"

                txtEmptyRequests.visibility =
                    View.VISIBLE

                recyclerRequests.visibility =
                    View.GONE

                txtRequestSummary.text =
                    "Could not load requests"

                Toast.makeText(
                    this,
                    "Failed to load requests: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // APPLY FILTER
    // =========================================================

    private fun applyFilter() {

        val filteredRequests =

            if (
                currentFilter.equals(
                    "All",
                    ignoreCase = true
                )
            ) {

                allRequests.toList()

            } else {

                allRequests.filter { request ->

                    request.status.equals(
                        currentFilter,
                        ignoreCase = true
                    )
                }
            }

        // =====================================================
        // SEND DATA TO ADAPTER
        // =====================================================

        adapter.updateRequests(
            filteredRequests
        )

        // =====================================================
        // EMPTY STATE
        // =====================================================

        if (filteredRequests.isEmpty()) {

            recyclerRequests.visibility =
                View.GONE

            txtEmptyRequests.visibility =
                View.VISIBLE

            txtEmptyRequests.text =

                if (
                    currentFilter.equals(
                        "All",
                        ignoreCase = true
                    )
                ) {

                    "No emergency requests found for $staffHospital"

                } else {

                    "No $currentFilter requests found"
                }

        } else {

            recyclerRequests.visibility =
                View.VISIBLE

            txtEmptyRequests.visibility =
                View.GONE
        }

        // =====================================================
        // SUMMARY
        // =====================================================

        txtRequestSummary.text =
            "${allRequests.size} request(s) for $staffHospital"
    }

    // =========================================================
    // SHOW NO HOSPITAL / ERROR
    // =========================================================

    private fun showNoHospital(
        message: String
    ) {

        allRequests.clear()

        if (::adapter.isInitialized) {

            adapter.updateRequests(
                emptyList()
            )
        }

        recyclerRequests.visibility =
            View.GONE

        txtEmptyRequests.visibility =
            View.VISIBLE

        txtEmptyRequests.text =
            message

        txtRequestSummary.text =
            "Hospital not available"
    }

    // =========================================================
    // FILTER DESIGN
    // =========================================================

    private fun selectFilter(
        selectedFilter: TextView
    ) {

        val filters =
            listOf(
                filterAll,
                filterPending,
                filterVerified,
                filterMatching,
                filterFulfilled,
                filterRejected
            )

        // =====================================================
        // RESET ALL FILTERS
        // =====================================================

        filters.forEach { filter ->

            filter.setBackgroundResource(
                R.drawable.bg_staff_filter
            )

            filter.setTextColor(
                android.graphics.Color.parseColor(
                    "#666666"
                )
            )
        }

        // =====================================================
        // SELECTED FILTER
        // =====================================================

        selectedFilter.setBackgroundResource(
            R.drawable.bg_staff_filter_selected
        )

        selectedFilter.setTextColor(
            android.graphics.Color.WHITE
        )
    }
}