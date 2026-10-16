
package com.example.blooddononerapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdminHospitalManagementActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val hospitals = mutableListOf<DocumentSnapshot>()
    private var listener: ListenerRegistration? = null

    private lateinit var edtSearch: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var txtCount: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var hospitalList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_hospital_management)

        edtSearch = findViewById(R.id.edtSearchHospital)
        spinnerStatus = findViewById(R.id.spinnerHospitalStatus)
        txtCount = findViewById(R.id.txtHospitalCount)
        txtEmpty = findViewById(R.id.txtEmptyHospital)
        hospitalList = findViewById(R.id.layoutHospitalList)

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnAddHospital)
            .setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        AdminHospitalDetailsActivity::class.java
                    ).putExtra("MODE", "ADD")
                )
            }

        val filters = listOf(
            "All Hospitals",
            "Active",
            "Inactive"
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
                    renderHospitals()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) = Unit
            }

        edtSearch.addTextChangedListener {
            renderHospitals()
        }
    }

    override fun onStart() {
        super.onStart()

        txtEmpty.text = "Loading hospitals..."
        txtEmpty.visibility = View.VISIBLE

        listener = db.collection("hospitals")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    hospitals.clear()
                    hospitalList.removeAllViews()
                    txtCount.text = "Unable to load centers"
                    txtEmpty.text =
                        "Failed to load hospitals: ${error.localizedMessage}"
                    txtEmpty.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                hospitals.clear()

                if (snapshot != null) {
                    hospitals.addAll(snapshot.documents)
                }

                hospitals.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) {
                        it.getString("name") ?: ""
                    }
                )

                renderHospitals()
            }
    }

    override fun onStop() {
        listener?.remove()
        listener = null
        super.onStop()
    }

    private fun hospitalStatus(
        hospital: DocumentSnapshot
    ): String {

        val storedStatus = hospital.getString("status")

        return when {
            storedStatus.equals(
                "Inactive",
                ignoreCase = true
            ) -> "Inactive"

            storedStatus.equals(
                "Suspended",
                ignoreCase = true
            ) -> "Inactive"

            storedStatus.equals(
                "Active",
                ignoreCase = true
            ) -> "Active"

            hospital.getBoolean("acceptingDonors") == false ->
                "Inactive"

            else -> "Active"
        }
    }

    private fun renderHospitals() {
        if (!::hospitalList.isInitialized) return

        val query = edtSearch.text.toString().trim()
        val filter = spinnerStatus.selectedItem?.toString()
            ?: "All Hospitals"

        txtCount.text = "${hospitals.size} Registered Centers"
        hospitalList.removeAllViews()

        val filtered = hospitals.filter { hospital ->
            val status = hospitalStatus(hospital)

            val matchesStatus =
                filter == "All Hospitals" || status == filter

            val searchText = listOf(
                hospital.getString("hospitalId") ?: hospital.id,
                hospital.getString("name") ?: "",
                hospital.getString("district") ?: "",
                hospital.getString("area") ?: ""
            ).joinToString(" ")

            matchesStatus &&
                    searchText.contains(query, ignoreCase = true)
        }

        txtEmpty.text =
            if (hospitals.isEmpty()) {
                "No registered hospitals yet. Tap Add Hospital to create one."
            } else {
                "No hospitals match your search or filter."
            }

        txtEmpty.visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE

        filtered.forEach { hospital ->
            hospitalList.addView(makeHospitalCard(hospital))
        }
    }

    private fun makeHospitalCard(
        hospital: DocumentSnapshot
    ): MaterialCardView {

        val status = hospitalStatus(hospital)

        val card = MaterialCardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = 0f
            strokeWidth = dp(1)
            strokeColor = Color.parseColor("#EEEEEE")
            setCardBackgroundColor(Color.WHITE)
            isClickable = true
            isFocusable = true

            setOnClickListener {
                startActivity(
                    Intent(
                        this@AdminHospitalManagementActivity,
                        AdminHospitalDetailsActivity::class.java
                    )
                        .putExtra("MODE", "EDIT")
                        .putExtra(
                            "HOSPITAL_DOCUMENT_ID",
                            hospital.id
                        )
                )
            }
        }

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(18),
                dp(15),
                dp(18),
                dp(15)
            )
        }

        val name = TextView(this).apply {
            text = hospital.getString("name")
                ?: "Unnamed Hospital"
            setTextColor(Color.parseColor("#222222"))
            textSize = 15f
            setTypeface(
                typeface,
                android.graphics.Typeface.BOLD
            )
        }

        val info = TextView(this).apply {
            text =
                "${hospital.getString("hospitalId") ?: hospital.id}  •  " +
                        listOfNotNull(
                            hospital.getString("area"),
                            hospital.getString("district")
                        )
                            .filter { it.isNotBlank() }
                            .joinToString(", ")

            setTextColor(Color.parseColor("#777777"))
            textSize = 12f
            setPadding(0, dp(5), 0, dp(5))
        }

        val state = TextView(this).apply {
            text = "$status  ›"
            textSize = 12f

            setTypeface(
                typeface,
                android.graphics.Typeface.BOLD
            )

            setTextColor(
                Color.parseColor(
                    if (status == "Active") {
                        "#15803D"
                    } else {
                        "#B91C1C"
                    }
                )
            )
        }

        content.addView(name)
        content.addView(info)
        content.addView(state)
        card.addView(content)

        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density)
            .toInt()
    }
}
