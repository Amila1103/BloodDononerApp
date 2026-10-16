package com.example.blooddononerapp

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

class AdminInventoryActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private var inventoryListener: ListenerRegistration? = null

    private val bloodKeys = listOf(
        "APlus", "AMinus", "BPlus", "BMinus",
        "ABPlus", "ABMinus", "OPlus", "OMinus"
    )

    private lateinit var btnRefresh: MaterialCardView
    private lateinit var txtTotalUnits: TextView
    private lateinit var txtLowStockCount: TextView
    private lateinit var txtAlertTitle: TextView
    private lateinit var txtTotalTitle: TextView
    private lateinit var groupViews: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_inventory)

        findViewById<MaterialCardView>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }

        btnRefresh = findViewById(R.id.btnRefresh)
        txtTotalUnits = findViewById(R.id.txtTotalUnits)
        txtLowStockCount = findViewById(R.id.txtLowStockCount)

        // Existing layout headings
        txtAlertTitle = (txtLowStockCount.parent as LinearLayout)
            .getChildAt(0) as TextView

        val totalCardColumn = txtTotalUnits.parent as LinearLayout
        txtTotalTitle = totalCardColumn.getChildAt(0) as TextView

        groupViews = linkedMapOf(
            "APlus" to findViewById(R.id.txtAPlus),
            "AMinus" to findViewById(R.id.txtAMinus),
            "BPlus" to findViewById(R.id.txtBPlus),
            "BMinus" to findViewById(R.id.txtBMinus),
            "ABPlus" to findViewById(R.id.txtABPlus),
            "ABMinus" to findViewById(R.id.txtABMinus),
            "OPlus" to findViewById(R.id.txtOPlus),
            "OMinus" to findViewById(R.id.txtOMinus)
        )

        showUnavailable("Loading inventory...")

        btnRefresh.setOnClickListener {
            refreshFromServer()
        }
    }

    override fun onStart() {
        super.onStart()

        showUnavailable("Loading inventory...")

        inventoryListener?.remove()

        inventoryListener = firestore.collection("hospitals")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->

                if (isFinishing || isDestroyed) {
                    return@addSnapshotListener
                }

                if (error != null) {
                    showUnavailable("Unable to load hospital stock")

                    Toast.makeText(
                        this,
                        "Firebase error: ${error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    renderInventory(snapshot)
                }
            }
    }

    override fun onStop() {
        inventoryListener?.remove()
        inventoryListener = null
        super.onStop()
    }

    private fun refreshFromServer() {

        btnRefresh.isEnabled = false

        firestore.collection("hospitals")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->

                if (isFinishing || isDestroyed) {
                    return@addOnSuccessListener
                }

                btnRefresh.isEnabled = true
                renderInventory(snapshot)

                Toast.makeText(
                    this,
                    "Inventory refreshed from Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { error ->

                if (isFinishing || isDestroyed) {
                    return@addOnFailureListener
                }

                btnRefresh.isEnabled = true

                Toast.makeText(
                    this,
                    "Refresh failed: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun renderInventory(snapshot: QuerySnapshot) {

        if (snapshot.isEmpty) {
            showUnavailable("No registered hospitals")
            return
        }

        val totals = bloodKeys
            .associateWith { 0L }
            .toMutableMap()

        var completeHospitals = 0
        var missingOrInvalidHospitals = 0

        for (hospital in snapshot.documents) {

            val stock = readCompleteStock(hospital)

            if (stock == null) {
                missingOrInvalidHospitals++
                continue
            }

            completeHospitals++

            for (key in bloodKeys) {
                totals[key] =
                    (totals[key] ?: 0L) + (stock[key] ?: 0L)
            }
        }

        if (completeHospitals == 0) {
            showUnavailable(
                "Hospital stock data missing or invalid"
            )
            return
        }

        val totalUnits = totals.values.sum()

        val lowStockGroups = totals.values.count {
            it <= 4L
        }

        val isPartial = missingOrInvalidHospitals > 0
        val fromCache = snapshot.metadata.isFromCache

        txtTotalUnits.text = if (isPartial) {
            "$totalUnits Units*"
        } else {
            "$totalUnits Units"
        }

        txtTotalTitle.text = when {
            fromCache -> "Cached Blood Stock"
            isPartial -> "Known Stock (*partial)"
            else -> "Total Blood Stock"
        }

        txtAlertTitle.text = when {
            isPartial -> "Incomplete Inventory Data"
            fromCache -> "Cached Inventory"
            else -> "Low Stock Alert"
        }

        txtLowStockCount.text = when {

            isPartial ->
                "$missingOrInvalidHospitals hospital(s) missing stock data"

            fromCache ->
                "$lowStockGroups groups low • awaiting server"

            else ->
                "$lowStockGroups Blood Groups (≤ 4 units)"
        }

        for (key in bloodKeys) {
            groupViews[key]?.let { view ->
                updateGroup(
                    view,
                    totals[key] ?: 0L,
                    fromCache
                )
            }
        }
    }

    private fun readCompleteStock(
        hospital: DocumentSnapshot
    ): Map<String, Long>? {

        val raw = hospital.get("bloodStock")
                as? Map<*, *> ?: return null

        val result = mutableMapOf<String, Long>()

        for (key in bloodKeys) {

            val number = raw[key] as? Number
                ?: return null

            val value = number.toDouble()

            if (
                !value.isFinite() ||
                value < 0.0 ||
                value % 1.0 != 0.0
            ) {
                return null
            }

            result[key] = number.toLong()
        }

        return result
    }

    private fun updateGroup(
        view: TextView,
        units: Long,
        fromCache: Boolean
    ) {

        view.text = if (units == 1L) {
            "1 Unit"
        } else {
            "$units Units"
        }

        val statusView = (view.parent as? LinearLayout)
            ?.getChildAt(2) as? TextView ?: return

        when {

            fromCache -> {
                statusView.text = "Cached"
                statusView.setTextColor(
                    Color.parseColor("#B45309")
                )
            }

            units == 0L -> {
                statusView.text = "Out of Stock"
                statusView.setTextColor(
                    Color.parseColor("#B91C1C")
                )
            }

            units <= 4L -> {
                statusView.text = "Low Stock"
                statusView.setTextColor(
                    Color.parseColor("#EF2637")
                )
            }

            else -> {
                statusView.text = "Available"
                statusView.setTextColor(
                    Color.parseColor("#15803D")
                )
            }
        }
    }

    private fun showUnavailable(message: String) {

        txtTotalUnits.text = "—"
        txtLowStockCount.text = message
        txtAlertTitle.text = "Inventory Status"
        txtTotalTitle.text = "Total Blood Stock"

        for (view in groupViews.values) {

            view.text = "—"

            val statusView = (view.parent as? LinearLayout)
                ?.getChildAt(2) as? TextView

            statusView?.text = "Not verified"

            statusView?.setTextColor(
                Color.parseColor("#777777")
            )
        }
    }
}
