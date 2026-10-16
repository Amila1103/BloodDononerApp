package com.example.blooddononerapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class BloodBankAvailabilityActivity : AppCompatActivity() {

    // =========================================================
    // BLOOD BANK MODEL
    // =========================================================

    data class BloodBank(
        val documentId: String = "",
        val name: String = "",
        val location: String = "",
        val acceptingDonors: Boolean = true,

        val aPlus: Int = 0,
        val aMinus: Int = 0,
        val bPlus: Int = 0,
        val bMinus: Int = 0,
        val abPlus: Int = 0,
        val abMinus: Int = 0,
        val oPlus: Int = 0,
        val oMinus: Int = 0
    )

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var firestore: FirebaseFirestore

    // =========================================================
    // UI
    // =========================================================

    private lateinit var bankContainer: LinearLayout
    private lateinit var searchInput: EditText

    private lateinit var btnNearby: TextView
    private lateinit var btnAll: TextView

    // =========================================================
    // DATA
    // =========================================================

    private val allBanks =
        mutableListOf<BloodBank>()

    private var selectedBloodGroup = "All"

    private var showNearby = true

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_blood_bank_availability
        )

        firestore =
            FirebaseFirestore.getInstance()

        // =====================================================
        // FIND VIEWS
        // =====================================================

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        searchInput =
            findViewById(
                R.id.searchInput
            )

        btnNearby =
            findViewById(
                R.id.btnNearby
            )

        btnAll =
            findViewById(
                R.id.btnAll
            )

        bankContainer =
            findViewById(
                R.id.bankContainer
            )

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()

            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }

        // =====================================================
        // NEARBY
        // =====================================================

        btnNearby.setOnClickListener {

            showNearby = true

            updateTabUI()

            showBanks()
        }

        // =====================================================
        // ALL
        // =====================================================

        btnAll.setOnClickListener {

            showNearby = false

            updateTabUI()

            showBanks()
        }

        // =====================================================
        // BLOOD GROUP FILTER
        // =====================================================

        setupBloodGroupFilters()

        // =====================================================
        // SEARCH
        // =====================================================

        searchInput.setOnEditorActionListener {
                _,
                _,
                _ ->

            showBanks()

            false
        }

        searchInput.setOnFocusChangeListener {
                _,
                hasFocus ->

            if (!hasFocus) {
                showBanks()
            }
        }

        // =====================================================
        // INITIAL
        // =====================================================

        updateTabUI()

        showLoading()

        loadHospitalsFromFirebase()
    }

    override fun onResume() {
        super.onResume()

        if (::firestore.isInitialized) {
            loadHospitalsFromFirebase()
        }
    }

    // =========================================================
    // FIREBASE - LOAD HOSPITALS
    // =========================================================

    private fun loadHospitalsFromFirebase() {

        firestore
            .collection("hospitals")
            .get()
            .addOnSuccessListener { snapshot ->

                allBanks.clear()

                for (document in snapshot.documents) {

                    val stock =
                        document.get("bloodStock")
                                as? Map<*, *>

                    val bank =
                        BloodBank(

                            documentId =
                                document.id,

                            name =
                                document.getString("name")
                                    ?: "Hospital",

                            // We currently do not save a separate
                            // location field when inventory is created.
                            // Use district/area/location if added later.
                            location =
                                getHospitalLocation(
                                    document.data
                                ),

                            acceptingDonors =
                                document.getBoolean(
                                    "acceptingDonors"
                                ) ?: true,

                            aPlus =
                                getStockValue(
                                    stock,
                                    "APlus"
                                ),

                            aMinus =
                                getStockValue(
                                    stock,
                                    "AMinus"
                                ),

                            bPlus =
                                getStockValue(
                                    stock,
                                    "BPlus"
                                ),

                            bMinus =
                                getStockValue(
                                    stock,
                                    "BMinus"
                                ),

                            abPlus =
                                getStockValue(
                                    stock,
                                    "ABPlus"
                                ),

                            abMinus =
                                getStockValue(
                                    stock,
                                    "ABMinus"
                                ),

                            oPlus =
                                getStockValue(
                                    stock,
                                    "OPlus"
                                ),

                            oMinus =
                                getStockValue(
                                    stock,
                                    "OMinus"
                                )
                        )

                    allBanks.add(bank)
                }

                showBanks()
            }
            .addOnFailureListener { exception ->

                allBanks.clear()

                showEmptyMessage(
                    "Unable to load blood banks."
                )

                Toast.makeText(
                    this,
                    "Failed to load hospitals: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // GET LOCATION
    // =========================================================

    private fun getHospitalLocation(
        data: Map<String, Any>?
    ): String {

        if (data == null) {
            return "Hospital"
        }

        val area =
            data["area"]?.toString()
                ?.trim()
                .orEmpty()

        val district =
            data["district"]?.toString()
                ?.trim()
                .orEmpty()

        val location =
            data["location"]?.toString()
                ?.trim()
                .orEmpty()

        return when {

            area.isNotBlank() &&
                    district.isNotBlank() ->

                "$area, $district"

            area.isNotBlank() ->
                area

            district.isNotBlank() ->
                district

            location.isNotBlank() ->
                location

            else ->
                "Hospital"
        }
    }

    // =========================================================
    // GET STOCK VALUE
    // =========================================================

    private fun getStockValue(
        stock: Map<*, *>?,
        key: String
    ): Int {

        val value =
            stock?.get(key)

        return when (value) {

            is Long ->
                value.toInt()

            is Int ->
                value

            is Double ->
                value.toInt()

            else ->
                0
        }
    }

    // =========================================================
    // BLOOD GROUP FILTERS
    // =========================================================

    private fun setupBloodGroupFilters() {

        val bloodGroups =
            mapOf(

                R.id.btnBloodAll to "All",

                R.id.btnAPlus to "A+",

                R.id.btnAMinus to "A-",

                R.id.btnBPlus to "B+",

                R.id.btnBMinus to "B-",

                R.id.btnABPlus to "AB+",

                R.id.btnABMinus to "AB-",

                R.id.btnOPlus to "O+",

                R.id.btnOMinus to "O-"
            )

        bloodGroups.forEach {
                (viewId, group) ->

            val view =
                findViewById<TextView>(
                    viewId
                )

            view.setOnClickListener {

                selectedBloodGroup =
                    group

                updateBloodGroupUI()

                showBanks()
            }
        }

        updateBloodGroupUI()
    }

    // =========================================================
    // BLOOD GROUP FILTER UI
    // =========================================================

    private fun updateBloodGroupUI() {

        val groups =
            mapOf(

                R.id.btnBloodAll to "All",

                R.id.btnAPlus to "A+",

                R.id.btnAMinus to "A-",

                R.id.btnBPlus to "B+",

                R.id.btnBMinus to "B-",

                R.id.btnABPlus to "AB+",

                R.id.btnABMinus to "AB-",

                R.id.btnOPlus to "O+",

                R.id.btnOMinus to "O-"
            )

        groups.forEach {
                (viewId, group) ->

            val view =
                findViewById<TextView>(
                    viewId
                )

            if (group == selectedBloodGroup) {

                view.setBackgroundResource(
                    R.drawable.bg_blood_group_selected
                )

                view.setTextColor(
                    getColor(
                        android.R.color.white
                    )
                )

            } else {

                view.setBackgroundResource(
                    R.drawable.bg_blood_group_normal
                )

                view.setTextColor(
                    getColor(
                        R.color.text_primary
                    )
                )
            }
        }
    }

    // =========================================================
    // TAB UI
    // =========================================================

    private fun updateTabUI() {

        if (showNearby) {

            btnNearby.setBackgroundResource(
                R.drawable.bg_tab_selected
            )

            btnNearby.setTextColor(
                getColor(
                    android.R.color.white
                )
            )

            btnAll.setBackgroundResource(
                android.R.color.transparent
            )

            btnAll.setTextColor(
                getColor(
                    R.color.text_secondary
                )
            )

        } else {

            btnAll.setBackgroundResource(
                R.drawable.bg_tab_selected
            )

            btnAll.setTextColor(
                getColor(
                    android.R.color.white
                )
            )

            btnNearby.setBackgroundResource(
                android.R.color.transparent
            )

            btnNearby.setTextColor(
                getColor(
                    R.color.text_secondary
                )
            )
        }
    }

    // =========================================================
    // SHOW BANKS
    // =========================================================

    private fun showBanks() {

        bankContainer.removeAllViews()

        // =====================================================
        // SEARCH
        // =====================================================

        val search =
            searchInput.text
                .toString()
                .trim()
                .lowercase()

        var filtered =
            allBanks.filter { bank ->

                search.isEmpty() ||

                        bank.name
                            .lowercase()
                            .contains(search) ||

                        bank.location
                            .lowercase()
                            .contains(search)
            }

        // =====================================================
        // BLOOD GROUP FILTER
        // =====================================================

        if (selectedBloodGroup != "All") {

            filtered =
                filtered.filter { bank ->

                    getUnitsForBloodGroup(
                        bank,
                        selectedBloodGroup
                    ) > 0
                }
        }

        // =====================================================
        // NEARBY
        // =====================================================
        //
        // Real GPS is not being used yet.
        // Keep the existing prototype behaviour:
        // Nearby = first 3 hospitals.
        //
        // =====================================================

        if (showNearby) {

            filtered =
                filtered.take(3)
        }

        // =====================================================
        // EMPTY
        // =====================================================

        if (filtered.isEmpty()) {

            val message =

                if (
                    selectedBloodGroup != "All"
                ) {

                    "No hospitals currently have $selectedBloodGroup blood available."

                } else {

                    "No blood banks found."
                }

            showEmptyMessage(message)

            return
        }

        // =====================================================
        // CARDS
        // =====================================================

        filtered.forEach { bank ->

            addBloodBankCard(bank)
        }
    }

    // =========================================================
    // ADD BLOOD BANK CARD
    // =========================================================

    private fun addBloodBankCard(
        bank: BloodBank
    ) {

        val card =
            layoutInflater.inflate(
                R.layout.item_blood_bank,
                bankContainer,
                false
            )

        val txtBankName =
            card.findViewById<TextView>(
                R.id.txtBankName
            )

        val txtLocation =
            card.findViewById<TextView>(
                R.id.txtLocation
            )

        val txtStock =
            card.findViewById<TextView>(
                R.id.txtStock
            )

        val txtDonorNeed =
            card.findViewById<TextView>(
                R.id.txtDonorNeed
            )

        val txtNeededGroups =
            card.findViewById<TextView>(
                R.id.txtNeededGroups
            )

        val txtDonationStatus =
            card.findViewById<TextView>(
                R.id.txtDonationStatus
            )

        val btnNeedBlood =
            card.findViewById<MaterialButton>(
                R.id.btnNeedBlood
            )

        val btnDonate =
            card.findViewById<MaterialButton>(
                R.id.btnDonate
            )

        val btnViewDetails =
            card.findViewById<TextView>(
                R.id.btnViewDetails
            )

        // =====================================================
        // STOCK CALCULATIONS
        // =====================================================

        val totalStock =
            getTotalStock(bank)

        val stockLevel =
            getStockLevel(totalStock)

        val neededGroups =
            getNeededGroups(bank)

        val donorNeed =
            getDonorNeed(bank)

        // =====================================================
        // SET DATA
        // =====================================================

        txtBankName.text =
            bank.name

        txtLocation.text =
            bank.location

        txtStock.text =
            "Blood Stock: $stockLevel ($totalStock Units)"

        txtDonorNeed.text =
            "Donor Need: $donorNeed"

        txtNeededGroups.text =

            if (neededGroups.isBlank()) {

                "Blood Needed: Stock currently available"

            } else {

                "Blood Needed: $neededGroups"
            }

        txtDonationStatus.text =

            if (bank.acceptingDonors) {

                "● Accepting donors now"

            } else {

                "Donation temporarily unavailable"
            }

        // =====================================================
        // NEED BLOOD
        // =====================================================

        btnNeedBlood.setOnClickListener {

            openEmergencyRequest(bank)
        }

        // =====================================================
        // DONATE
        // =====================================================

        btnDonate.setOnClickListener {

            openDonateBlood(
                bank,
                neededGroups
            )
        }

        // =====================================================
        // DETAILS
        // =====================================================

        btnViewDetails.setOnClickListener {

            showBloodBankDetails(bank)
        }

        bankContainer.addView(card)
    }

    // =========================================================
    // TOTAL STOCK
    // =========================================================

    private fun getTotalStock(
        bank: BloodBank
    ): Int {

        return bank.aPlus +
                bank.aMinus +
                bank.bPlus +
                bank.bMinus +
                bank.abPlus +
                bank.abMinus +
                bank.oPlus +
                bank.oMinus
    }

    // =========================================================
    // STOCK LEVEL
    // =========================================================

    private fun getStockLevel(
        total: Int
    ): String {

        return when {

            total <= 5 ->
                "Critical"

            total <= 20 ->
                "Low"

            total <= 50 ->
                "Medium"

            else ->
                "High"
        }
    }

    // =========================================================
    // DONOR NEED
    // =========================================================

    private fun getDonorNeed(
        bank: BloodBank
    ): String {

        val values =
            listOf(
                bank.aPlus,
                bank.aMinus,
                bank.bPlus,
                bank.bMinus,
                bank.abPlus,
                bank.abMinus,
                bank.oPlus,
                bank.oMinus
            )

        return when {

            values.any {
                it <= 2
            } ->
                "Urgent"

            values.any {
                it <= 5
            } ->
                "Medium"

            else ->
                "Low"
        }
    }

    // =========================================================
    // NEEDED GROUPS
    // =========================================================

    private fun getNeededGroups(
        bank: BloodBank
    ): String {

        val groups =
            mutableListOf<String>()

        // <= 5 units = donor needed

        if (bank.aPlus <= 5) {
            groups.add("A+")
        }

        if (bank.aMinus <= 5) {
            groups.add("A-")
        }

        if (bank.bPlus <= 5) {
            groups.add("B+")
        }

        if (bank.bMinus <= 5) {
            groups.add("B-")
        }

        if (bank.abPlus <= 5) {
            groups.add("AB+")
        }

        if (bank.abMinus <= 5) {
            groups.add("AB-")
        }

        if (bank.oPlus <= 5) {
            groups.add("O+")
        }

        if (bank.oMinus <= 5) {
            groups.add("O-")
        }

        return groups.joinToString(", ")
    }

    // =========================================================
    // GET SELECTED BLOOD GROUP UNITS
    // =========================================================

    private fun getUnitsForBloodGroup(
        bank: BloodBank,
        group: String
    ): Int {

        return when (group) {

            "A+" -> bank.aPlus
            "A-" -> bank.aMinus

            "B+" -> bank.bPlus
            "B-" -> bank.bMinus

            "AB+" -> bank.abPlus
            "AB-" -> bank.abMinus

            "O+" -> bank.oPlus
            "O-" -> bank.oMinus

            else -> getTotalStock(bank)
        }
    }

    // =========================================================
    // EMERGENCY REQUEST
    // =========================================================

    private fun openEmergencyRequest(
        bank: BloodBank
    ) {

        val intent =
            Intent(
                this,
                EmergencyRequestActivity::class.java
            )

        intent.putExtra(
            "hospital_name",
            bank.name
        )

        intent.putExtra(
            "hospital_location",
            bank.location
        )

        startActivity(intent)

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    // =========================================================
    // DONATE BLOOD
    // =========================================================

    private fun openDonateBlood(
        bank: BloodBank,
        neededGroups: String
    ) {

        val intent =
            Intent(
                this,
                DonateBloodActivity::class.java
            )

        intent.putExtra(
            "hospital_name",
            bank.name
        )

        intent.putExtra(
            "hospital_location",
            bank.location
        )

        intent.putExtra(
            "needed_groups",
            neededGroups
        )

        startActivity(intent)

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    // =========================================================
    // DETAILS
    // =========================================================

    private fun showBloodBankDetails(
        bank: BloodBank
    ) {

        val donationStatus =

            if (bank.acceptingDonors) {

                "Accepting donors now"

            } else {

                "Donation temporarily unavailable"
            }

        val total =
            getTotalStock(bank)

        val details =
            """
Location
${bank.location}

BLOOD INVENTORY

A+   ${bank.aPlus} Units
A-   ${bank.aMinus} Units

B+   ${bank.bPlus} Units
B-   ${bank.bMinus} Units

AB+  ${bank.abPlus} Units
AB-  ${bank.abMinus} Units

O+   ${bank.oPlus} Units
O-   ${bank.oMinus} Units

Total Blood Stock
$total Units

Current Stock Level
${getStockLevel(total)}

Current Donor Need
${getDonorNeed(bank)}

Blood Groups Needed
${getNeededGroups(bank).ifBlank { "None currently" }}

Donation Status
$donationStatus


IMPORTANT INFORMATION

Blood stock can change frequently.

Please contact the selected hospital or
blood bank for urgent requirements.

Final donor eligibility is determined
by qualified medical staff.
            """.trimIndent()

        val dialog =
            androidx.appcompat.app.AlertDialog
                .Builder(this)
                .setTitle(
                    bank.name
                )
                .setMessage(
                    details
                )
                .setNegativeButton(
                    "Close",
                    null
                )
                .setNeutralButton(
                    "Need Blood"
                ) { _, _ ->

                    openEmergencyRequest(
                        bank
                    )
                }
                .setPositiveButton(
                    "Donate Blood"
                ) { _, _ ->

                    openDonateBlood(
                        bank,
                        getNeededGroups(bank)
                    )
                }
                .create()

        dialog.show()

        dialog.getButton(
            androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
        ).setTextColor(
            getColor(
                R.color.blood_red
            )
        )

        dialog.getButton(
            androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL
        ).setTextColor(
            getColor(
                R.color.blood_red
            )
        )

        dialog.getButton(
            androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE
        ).setTextColor(
            getColor(
                R.color.text_secondary
            )
        )
    }

    // =========================================================
    // LOADING
    // =========================================================

    private fun showLoading() {

        bankContainer.removeAllViews()

        val loading =
            TextView(this)

        loading.text =
            "Loading blood bank availability..."

        loading.textSize =
            13f

        loading.gravity =
            Gravity.CENTER

        loading.setTextColor(
            getColor(
                R.color.text_secondary
            )
        )

        loading.setPadding(
            20,
            70,
            20,
            70
        )

        bankContainer.addView(
            loading
        )
    }

    // =========================================================
    // EMPTY MESSAGE
    // =========================================================

    private fun showEmptyMessage(
        message: String
    ) {

        bankContainer.removeAllViews()

        val empty =
            TextView(this)

        empty.text =
            message

        empty.textSize =
            13f

        empty.gravity =
            Gravity.CENTER

        empty.setTextColor(
            getColor(
                R.color.text_secondary
            )
        )

        empty.setPadding(
            20,
            70,
            20,
            70
        )

        bankContainer.addView(
            empty
        )
    }
}