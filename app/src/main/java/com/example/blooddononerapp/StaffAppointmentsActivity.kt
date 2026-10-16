package com.example.blooddononerapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StaffAppointmentsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var filterToday: TextView
    private lateinit var filterUpcoming: TextView
    private lateinit var filterCompleted: TextView
    private lateinit var filterCancelled: TextView

    private lateinit var appointmentContainer: LinearLayout
    private lateinit var txtEmptyAppointments: TextView
    private lateinit var txtAppointmentSummary: TextView

    private val allAppointments =
        mutableListOf<DonationAppointment>()

    private var staffHospital = ""
    private var currentFilter = "Today"

    data class DonationAppointment(
        val documentId: String = "",
        val appointmentId: String = "",
        val userId: String = "",
        val donorName: String = "",
        val donorId: String = "",
        val phone: String = "",
        val bloodGroup: String = "",
        val hospital: String = "",
        val location: String = "",
        val donationDate: String = "",
        val donationTime: String = "",
        val status: String = "Pending",
        val checkedIn: Boolean = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_appointments
        )

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<MaterialCardView>(
                R.id.btnBack
            )

        val btnRefresh =
            findViewById<MaterialCardView>(
                R.id.btnRefresh
            )

        filterToday =
            findViewById(R.id.filterToday)

        filterUpcoming =
            findViewById(R.id.filterUpcoming)

        filterCompleted =
            findViewById(R.id.filterCompleted)

        filterCancelled =
            findViewById(R.id.filterCancelled)

        appointmentContainer =
            findViewById(
                R.id.appointmentContainer
            )

        txtEmptyAppointments =
            findViewById(
                R.id.txtEmptyAppointments
            )

        txtAppointmentSummary =
            findViewById(
                R.id.txtAppointmentSummary
            )

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // REFRESH
        // =====================================================

        btnRefresh.setOnClickListener {

            loadStaffHospital()

            Toast.makeText(
                this,
                "Appointments refreshed",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================================
        // FILTERS
        // =====================================================

        filterToday.setOnClickListener {

            currentFilter = "Today"

            selectFilter(
                filterToday
            )

            applyFilter()
        }

        filterUpcoming.setOnClickListener {

            currentFilter = "Upcoming"

            selectFilter(
                filterUpcoming
            )

            applyFilter()
        }

        filterCompleted.setOnClickListener {

            currentFilter = "Completed"

            selectFilter(
                filterCompleted
            )

            applyFilter()
        }

        filterCancelled.setOnClickListener {

            currentFilter = "Cancelled"

            selectFilter(
                filterCancelled
            )

            applyFilter()
        }

        selectFilter(
            filterToday
        )

        showLoading()

        loadStaffHospital()
    }

    override fun onResume() {
        super.onResume()

        if (::firestore.isInitialized) {
            loadStaffHospital()
        }
    }

    // =========================================================
    // LOAD STAFF HOSPITAL
    // =========================================================

    private fun loadStaffHospital() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            showEmpty(
                "Staff account not found."
            )

            return
        }

        firestore
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    showEmpty(
                        "Staff profile not found."
                    )

                    return@addOnSuccessListener
                }

                val role =
                    document.getString("role")
                        ?: ""

                if (!role.equals(
                        "STAFF",
                        ignoreCase = true
                    )
                ) {

                    showEmpty(
                        "This account is not a staff account."
                    )

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

                    showEmpty(
                        "No hospital assigned to this staff account."
                    )

                    return@addOnSuccessListener
                }

                txtAppointmentSummary.text =
                    staffHospital

                loadAppointments()
            }
            .addOnFailureListener { exception ->

                showEmpty(
                    "Unable to load staff hospital."
                )

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // LOAD FIREBASE APPOINTMENTS
    // =========================================================

    private fun loadAppointments() {

        showLoading()

        /*
         * We filter by hospital in Firebase.
         *
         * We do NOT use orderBy here because combining
         * whereEqualTo + orderBy may require a composite index.
         */

        firestore
            .collection("donationAppointments")
            .whereEqualTo(
                "hospital",
                staffHospital
            )
            .get()
            .addOnSuccessListener { snapshot ->

                allAppointments.clear()

                for (document in snapshot.documents) {

                    val appointment =
                        DonationAppointment(

                            documentId =
                                document.id,

                            appointmentId =
                                document.getString(
                                    "appointmentId"
                                ) ?: document.id,

                            userId =
                                document.getString(
                                    "userId"
                                ) ?: "",

                            donorName =
                                document.getString(
                                    "donorName"
                                ) ?: "Donor",

                            donorId =
                                document.getString(
                                    "donorId"
                                ) ?: "",

                            phone =
                                document.getString(
                                    "phone"
                                ) ?: "",

                            bloodGroup =
                                document.getString(
                                    "bloodGroup"
                                ) ?: "-",

                            hospital =
                                document.getString(
                                    "hospital"
                                ) ?: "",

                            location =
                                document.getString(
                                    "location"
                                ) ?: "",

                            donationDate =
                                document.getString(
                                    "donationDate"
                                ) ?: "",

                            donationTime =
                                document.getString(
                                    "donationTime"
                                ) ?: "",

                            status =
                                document.getString(
                                    "status"
                                ) ?: "Pending",

                            checkedIn =
                                document.getBoolean(
                                    "checkedIn"
                                ) ?: false
                        )

                    allAppointments.add(
                        appointment
                    )
                }

                /*
                 * Newly-created documents normally arrive in
                 * arbitrary order because we intentionally
                 * avoided orderBy.
                 */

                applyFilter()
            }
            .addOnFailureListener { exception ->

                showEmpty(
                    "Unable to load appointments."
                )

                Toast.makeText(
                    this,
                    "Failed to load appointments: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // APPLY FILTER
    // =========================================================

    private fun applyFilter() {

        appointmentContainer.removeAllViews()

        val filtered =
            when (currentFilter) {

                "Completed" -> {

                    allAppointments.filter {

                        it.status.equals(
                            "Completed",
                            ignoreCase = true
                        )
                    }
                }

                "Cancelled" -> {

                    allAppointments.filter {

                        it.status.equals(
                            "Cancelled",
                            ignoreCase = true
                        ) ||
                                it.status.equals(
                                    "Rejected",
                                    ignoreCase = true
                                )
                    }
                }

                "Upcoming" -> {

                    /*
                     * Current appointment data stores the date
                     * as display text, e.g. "30 Sep 2026".
                     *
                     * For now, all active appointments are
                     * available under Upcoming.
                     */

                    allAppointments.filter {

                        !it.status.equals(
                            "Completed",
                            ignoreCase = true
                        ) &&
                                !it.status.equals(
                                    "Cancelled",
                                    ignoreCase = true
                                ) &&
                                !it.status.equals(
                                    "Rejected",
                                    ignoreCase = true
                                )
                    }
                }

                else -> {

                    /*
                     * The existing UI has a Today filter.
                     * Since donationDate is stored as
                     * "30 Sep 2026", compare it with today's
                     * date using the same format.
                     */

                    val today =
                        java.text.SimpleDateFormat(
                            "dd MMM yyyy",
                            java.util.Locale.ENGLISH
                        ).format(
                            java.util.Date()
                        )

                    allAppointments.filter {

                        it.donationDate.equals(
                            today,
                            ignoreCase = true
                        ) &&
                                !it.status.equals(
                                    "Completed",
                                    ignoreCase = true
                                ) &&
                                !it.status.equals(
                                    "Cancelled",
                                    ignoreCase = true
                                ) &&
                                !it.status.equals(
                                    "Rejected",
                                    ignoreCase = true
                                )
                    }
                }
            }

        txtAppointmentSummary.text =
            "${filtered.size} appointment(s) • $staffHospital"

        if (filtered.isEmpty()) {

            txtEmptyAppointments.visibility =
                View.VISIBLE

            txtEmptyAppointments.text =
                when (currentFilter) {

                    "Today" ->
                        "No appointments for today"

                    "Upcoming" ->
                        "No upcoming appointments"

                    "Completed" ->
                        "No completed appointments"

                    "Cancelled" ->
                        "No cancelled appointments"

                    else ->
                        "No appointments found"
                }

            return
        }

        txtEmptyAppointments.visibility =
            View.GONE

        filtered.forEach { appointment ->

            addAppointmentCard(
                appointment
            )
        }
    }

    // =========================================================
    // ADD DYNAMIC CARD
    // =========================================================

    private fun addAppointmentCard(
        appointment: DonationAppointment
    ) {

        val card =
            layoutInflater.inflate(
                R.layout.item_staff_appointment,
                appointmentContainer,
                false
            )

        val txtBloodGroup =
            card.findViewById<TextView>(
                R.id.txtBloodGroup
            )

        val txtDonorName =
            card.findViewById<TextView>(
                R.id.txtDonorName
            )

        val txtDonorId =
            card.findViewById<TextView>(
                R.id.txtDonorId
            )

        val txtStatus =
            card.findViewById<TextView>(
                R.id.txtStatus
            )

        val txtDateTime =
            card.findViewById<TextView>(
                R.id.txtDateTime
            )

        val txtHospital =
            card.findViewById<TextView>(
                R.id.txtHospital
            )

        txtBloodGroup.text =
            appointment.bloodGroup

        txtDonorName.text =
            appointment.donorName

        txtDonorId.text =
            appointment.donorId

        txtStatus.text =
            getDisplayStatus(
                appointment
            )

        txtDateTime.text =
            "${appointment.donationDate}  •  ${appointment.donationTime}"

        txtHospital.text =
            appointment.hospital

        card.setOnClickListener {

            openAppointmentDetails(
                appointment
            )
        }

        appointmentContainer.addView(
            card
        )
    }

    // =========================================================
    // DISPLAY STATUS
    // =========================================================

    private fun getDisplayStatus(
        appointment: DonationAppointment
    ): String {

        if (appointment.checkedIn) {
            return "Checked In"
        }

        return appointment.status
    }

    // =========================================================
    // OPEN DETAILS
    // =========================================================

    private fun openAppointmentDetails(
        appointment: DonationAppointment
    ) {

        val intent =
            Intent(
                this,
                StaffAppointmentDetailsActivity::class.java
            )

        /*
         * Important:
         * DOCUMENT_ID is the actual Firestore document ID.
         * We will use this in the next step for Check In.
         */

        intent.putExtra(
            "DOCUMENT_ID",
            appointment.documentId
        )

        intent.putExtra(
            "APPOINTMENT_ID",
            appointment.appointmentId
        )

        intent.putExtra(
            "DONOR_NAME",
            appointment.donorName
        )

        intent.putExtra(
            "DONOR_ID",
            appointment.donorId
        )

        intent.putExtra(
            "BLOOD_GROUP",
            appointment.bloodGroup
        )

        intent.putExtra(
            "DATE",
            appointment.donationDate
        )

        intent.putExtra(
            "TIME",
            appointment.donationTime
        )

        intent.putExtra(
            "CENTER",
            appointment.hospital
        )

        intent.putExtra(
            "LOCATION",
            appointment.location
        )

        intent.putExtra(
            "PHONE",
            appointment.phone
        )

        intent.putExtra(
            "STATUS",
            appointment.status
        )

        intent.putExtra(
            "CHECKED_IN",
            appointment.checkedIn
        )

        startActivity(intent)

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    // =========================================================
    // FILTER UI
    // =========================================================

    private fun selectFilter(
        selected: TextView
    ) {

        val filters =
            listOf(
                filterToday,
                filterUpcoming,
                filterCompleted,
                filterCancelled
            )

        filters.forEach {

            it.setBackgroundResource(
                R.drawable.bg_staff_filter
            )

            it.setTextColor(
                Color.parseColor(
                    "#666666"
                )
            )
        }

        selected.setBackgroundResource(
            R.drawable.bg_staff_filter_selected
        )

        selected.setTextColor(
            Color.WHITE
        )
    }

    // =========================================================
    // LOADING
    // =========================================================

    private fun showLoading() {

        if (!::appointmentContainer.isInitialized) {
            return
        }

        appointmentContainer.removeAllViews()

        txtEmptyAppointments.visibility =
            View.GONE

        val loading =
            TextView(this)

        loading.text =
            "Loading appointments..."

        loading.textSize =
            13f

        loading.gravity =
            Gravity.CENTER

        loading.setTextColor(
            Color.parseColor(
                "#777777"
            )
        )

        loading.setPadding(
            20,
            70,
            20,
            70
        )

        appointmentContainer.addView(
            loading
        )
    }

    // =========================================================
    // EMPTY
    // =========================================================

    private fun showEmpty(
        message: String
    ) {

        appointmentContainer.removeAllViews()

        txtEmptyAppointments.visibility =
            View.VISIBLE

        txtEmptyAppointments.text =
            message
    }
}