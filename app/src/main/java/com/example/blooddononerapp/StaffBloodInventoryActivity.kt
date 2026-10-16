package com.example.blooddononerapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StaffBloodInventoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var txtHospitalName: TextView

    private lateinit var txtAPlus: TextView
    private lateinit var txtAMinus: TextView
    private lateinit var txtBPlus: TextView
    private lateinit var txtBMinus: TextView
    private lateinit var txtABPlus: TextView
    private lateinit var txtABMinus: TextView
    private lateinit var txtOPlus: TextView
    private lateinit var txtOMinus: TextView

    private lateinit var btnUpdateStock: MaterialButton

    private var staffHospital = ""
    private var hospitalDocumentId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_staff_blood_inventory
        )

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<TextView>(
                R.id.btnBack
            )

        txtHospitalName =
            findViewById(
                R.id.txtHospitalName
            )

        txtAPlus =
            findViewById(R.id.txtAPlus)

        txtAMinus =
            findViewById(R.id.txtAMinus)

        txtBPlus =
            findViewById(R.id.txtBPlus)

        txtBMinus =
            findViewById(R.id.txtBMinus)

        txtABPlus =
            findViewById(R.id.txtABPlus)

        txtABMinus =
            findViewById(R.id.txtABMinus)

        txtOPlus =
            findViewById(R.id.txtOPlus)

        txtOMinus =
            findViewById(R.id.txtOMinus)

        btnUpdateStock =
            findViewById(
                R.id.btnUpdateStock
            )

        // Don't allow update until hospital is loaded
        btnUpdateStock.isEnabled = false

        setLoadingStock()

        btnBack.setOnClickListener {
            finish()
        }

        btnUpdateStock.setOnClickListener {

            if (staffHospital.isBlank()) {

                Toast.makeText(
                    this,
                    "Hospital not available",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                showUpdateDialog()
            }
        }

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

        txtHospitalName.text =
            "Loading hospital..."

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
                        ?: ""

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
                        ?: ""

                if (
                    staffHospital.isBlank() ||
                    staffHospital.equals(
                        "Not Assigned",
                        ignoreCase = true
                    )
                ) {

                    txtHospitalName.text =
                        "No hospital assigned"

                    Toast.makeText(
                        this,
                        "No hospital assigned to this staff account",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                txtHospitalName.text =
                    staffHospital

                findOrCreateHospital()
            }
            .addOnFailureListener { exception ->

                txtHospitalName.text =
                    "Unable to load hospital"

                Toast.makeText(
                    this,
                    "Failed to load staff hospital: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // FIND HOSPITAL DOCUMENT
    // =========================================================

    private fun findOrCreateHospital() {

        firestore
            .collection("hospitals")
            .whereEqualTo(
                "name",
                staffHospital
            )
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                if (!result.isEmpty) {

                    val hospitalDocument =
                        result.documents[0]

                    hospitalDocumentId =
                        hospitalDocument.id

                    loadBloodStock()

                } else {

                    createHospitalDocument()
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to find hospital: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // CREATE HOSPITAL IF IT DOES NOT EXIST
    // =========================================================

    private fun createHospitalDocument() {

        val document =
            firestore
                .collection("hospitals")
                .document()

        hospitalDocumentId =
            document.id

        val bloodStock =
            hashMapOf<String, Any>(
                "APlus" to 0,
                "AMinus" to 0,
                "BPlus" to 0,
                "BMinus" to 0,
                "ABPlus" to 0,
                "ABMinus" to 0,
                "OPlus" to 0,
                "OMinus" to 0
            )

        val hospitalData =
            hashMapOf<String, Any>(
                "hospitalId" to hospitalDocumentId,
                "name" to staffHospital,
                "bloodStock" to bloodStock,
                "acceptingDonors" to true,
                "createdAt" to
                        FieldValue.serverTimestamp(),
                "updatedAt" to
                        FieldValue.serverTimestamp()
            )

        document
            .set(hospitalData)
            .addOnSuccessListener {

                setAllStockToZero()

                btnUpdateStock.isEnabled =
                    true

                Toast.makeText(
                    this,
                    "Hospital inventory created",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to create inventory: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // LOAD BLOOD STOCK
    // =========================================================

    private fun loadBloodStock() {

        if (hospitalDocumentId.isBlank()) {
            return
        }

        firestore
            .collection("hospitals")
            .document(hospitalDocumentId)
            .get()
            .addOnSuccessListener { document ->

                val bloodStock =
                    document.get(
                        "bloodStock"
                    ) as? Map<*, *>

                val aPlus =
                    getStockValue(
                        bloodStock,
                        "APlus"
                    )

                val aMinus =
                    getStockValue(
                        bloodStock,
                        "AMinus"
                    )

                val bPlus =
                    getStockValue(
                        bloodStock,
                        "BPlus"
                    )

                val bMinus =
                    getStockValue(
                        bloodStock,
                        "BMinus"
                    )

                val abPlus =
                    getStockValue(
                        bloodStock,
                        "ABPlus"
                    )

                val abMinus =
                    getStockValue(
                        bloodStock,
                        "ABMinus"
                    )

                val oPlus =
                    getStockValue(
                        bloodStock,
                        "OPlus"
                    )

                val oMinus =
                    getStockValue(
                        bloodStock,
                        "OMinus"
                    )

                txtAPlus.text =
                    formatUnits(aPlus)

                txtAMinus.text =
                    formatUnits(aMinus)

                txtBPlus.text =
                    formatUnits(bPlus)

                txtBMinus.text =
                    formatUnits(bMinus)

                txtABPlus.text =
                    formatUnits(abPlus)

                txtABMinus.text =
                    formatUnits(abMinus)

                txtOPlus.text =
                    formatUnits(oPlus)

                txtOMinus.text =
                    formatUnits(oMinus)

                btnUpdateStock.isEnabled =
                    true
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load blood stock: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // READ NUMBER SAFELY FROM FIRESTORE
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
    // UPDATE DIALOG
    // =========================================================

    private fun showUpdateDialog() {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_update_blood_stock,
                null
            )

        val edtBloodGroup =
            view.findViewById<TextInputEditText>(
                R.id.edtBloodGroup
            )

        val edtUnits =
            view.findViewById<TextInputEditText>(
                R.id.edtUnits
            )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Update Blood Stock")
                .setView(view)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Update",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val group =
                    edtBloodGroup.text
                        ?.toString()
                        ?.trim()
                        ?.uppercase()
                        .orEmpty()

                val units =
                    edtUnits.text
                        ?.toString()
                        ?.trim()
                        ?.toIntOrNull()

                if (units == null || units < 0) {

                    edtUnits.error =
                        "Enter valid units"

                    return@setOnClickListener
                }

                val firestoreKey =
                    bloodGroupToFirestoreKey(
                        group
                    )

                if (firestoreKey == null) {

                    edtBloodGroup.error =
                        "Use A+, A-, B+, B-, AB+, AB-, O+ or O-"

                    return@setOnClickListener
                }

                saveBloodStock(
                    group = group,
                    firestoreKey = firestoreKey,
                    units = units,
                    dialog = dialog
                )
            }
        }

        dialog.show()
    }

    // =========================================================
    // CONVERT BLOOD GROUP TO FIRESTORE KEY
    // =========================================================

    private fun bloodGroupToFirestoreKey(
        group: String
    ): String? {

        return when (group) {

            "A+" -> "APlus"
            "A-" -> "AMinus"

            "B+" -> "BPlus"
            "B-" -> "BMinus"

            "AB+" -> "ABPlus"
            "AB-" -> "ABMinus"

            "O+" -> "OPlus"
            "O-" -> "OMinus"

            else -> null
        }
    }

    // =========================================================
    // SAVE STOCK TO FIRESTORE
    // =========================================================

    private fun saveBloodStock(
        group: String,
        firestoreKey: String,
        units: Int,
        dialog: AlertDialog
    ) {

        if (hospitalDocumentId.isBlank()) {

            Toast.makeText(
                this,
                "Hospital inventory not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val updates =
            hashMapOf<String, Any>(
                "bloodStock.$firestoreKey" to units,
                "updatedAt" to
                        FieldValue.serverTimestamp()
            )

        firestore
            .collection("hospitals")
            .document(hospitalDocumentId)
            .update(updates)
            .addOnSuccessListener {

                updateBloodGroupUI(
                    group,
                    units
                )

                Toast.makeText(
                    this,
                    "$group stock updated to $units units",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to update stock: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // UPDATE SCREEN
    // =========================================================

    private fun updateBloodGroupUI(
        group: String,
        units: Int
    ) {

        val value =
            formatUnits(units)

        when (group) {

            "A+" ->
                txtAPlus.text = value

            "A-" ->
                txtAMinus.text = value

            "B+" ->
                txtBPlus.text = value

            "B-" ->
                txtBMinus.text = value

            "AB+" ->
                txtABPlus.text = value

            "AB-" ->
                txtABMinus.text = value

            "O+" ->
                txtOPlus.text = value

            "O-" ->
                txtOMinus.text = value
        }
    }

    // =========================================================
    // DISPLAY HELPERS
    // =========================================================

    private fun formatUnits(
        units: Int
    ): String {

        return if (units == 1) {
            "1 Unit"
        } else {
            "$units Units"
        }
    }

    private fun setLoadingStock() {

        txtAPlus.text = "--"
        txtAMinus.text = "--"
        txtBPlus.text = "--"
        txtBMinus.text = "--"
        txtABPlus.text = "--"
        txtABMinus.text = "--"
        txtOPlus.text = "--"
        txtOMinus.text = "--"
    }

    private fun setAllStockToZero() {

        txtAPlus.text = "0 Units"
        txtAMinus.text = "0 Units"
        txtBPlus.text = "0 Units"
        txtBMinus.text = "0 Units"
        txtABPlus.text = "0 Units"
        txtABMinus.text = "0 Units"
        txtOPlus.text = "0 Units"
        txtOMinus.text = "0 Units"
    }
}