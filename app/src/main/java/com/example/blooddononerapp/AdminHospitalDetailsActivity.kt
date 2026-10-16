
package com.example.blooddononerapp

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class AdminHospitalDetailsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var edtId: TextInputEditText
    private lateinit var edtName: TextInputEditText
    private lateinit var edtDistrict: TextInputEditText
    private lateinit var edtArea: TextInputEditText
    private lateinit var edtPhone: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var txtStatus: TextView
    private lateinit var btnStatus: MaterialButton
    private lateinit var btnSave: MaterialButton

    private var documentId: String? = null
    private var isAddMode = false
    private var currentStatus = "Active"
    private var busy = false
    private var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_hospital_details)

        edtId = findViewById(R.id.edtHospitalId)
        edtName = findViewById(R.id.edtHospitalName)
        edtDistrict = findViewById(R.id.edtDistrict)
        edtArea = findViewById(R.id.edtArea)
        edtPhone = findViewById(R.id.edtHospitalPhone)
        edtEmail = findViewById(R.id.edtHospitalEmail)

        txtStatus = findViewById(R.id.txtHospitalStatus)
        btnStatus = findViewById(R.id.btnToggleHospitalStatus)
        btnSave = findViewById(R.id.btnSaveHospital)

        findViewById<MaterialButton>(R.id.btnHospitalBack)
            .setOnClickListener {
                finish()
            }

        isAddMode = intent.getStringExtra("MODE") == "ADD"
        documentId = intent.getStringExtra("HOSPITAL_DOCUMENT_ID")

        btnStatus.visibility =
            if (isAddMode) View.GONE else View.VISIBLE

        btnSave.text =
            if (isAddMode) "Add Hospital" else "Save Changes"

        txtStatus.text = "Status: Active"

        btnSave.setOnClickListener {
            validateAndSave()
        }

        btnStatus.setOnClickListener {
            confirmStatusChange()
        }

        if (isAddMode) {
            edtId.setText("Generated when saved")
            loaded = true
        } else if (documentId.isNullOrBlank()) {
            showMessage("Hospital document ID is missing")
            finish()
        } else {
            loadHospital()
        }

        updateButtons()
    }

    private fun loadHospital() {
        val id = documentId ?: return

        busy = true
        updateButtons()

        db.collection("hospitals")
            .document(id)
            .get(Source.SERVER)
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    showMessage("Hospital not found")
                    finish()
                    return@addOnSuccessListener
                }

                edtId.setText(
                    document.getString("hospitalId") ?: document.id
                )

                edtName.setText(
                    document.getString("name") ?: ""
                )

                edtDistrict.setText(
                    document.getString("district") ?: ""
                )

                edtArea.setText(
                    document.getString("area") ?: ""
                )

                edtPhone.setText(
                    document.getString("phone") ?: ""
                )

                edtEmail.setText(
                    document.getString("email") ?: ""
                )

                currentStatus = getStatus(document)
                loaded = true
                busy = false

                // Hospital names are used by staff records.
                // Lock existing names to avoid broken assignments.
                edtName.isEnabled = false

                updateButtons()
            }
            .addOnFailureListener { exception ->

                busy = false

                showMessage(
                    "Could not load hospital: ${exception.localizedMessage}"
                )

                updateButtons()
            }
    }

    private fun getStatus(
        document: DocumentSnapshot
    ): String {

        val status = document.getString("status")

        return when {
            status.equals("Inactive", true) ||
                    status.equals("Suspended", true) -> "Inactive"

            status.equals("Active", true) -> "Active"

            document.getBoolean("acceptingDonors") == false ->
                "Inactive"

            else -> "Active"
        }
    }

    private fun validateAndSave() {

        if (busy || !loaded) return

        val name = edtName.text.toString().trim()
        val district = edtDistrict.text.toString().trim()
        val area = edtArea.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isEmpty()) {
            edtName.error = "Enter hospital name"
            return
        }

        if (district.isEmpty()) {
            edtDistrict.error = "Enter district"
            return
        }

        if (area.isEmpty()) {
            edtArea.error = "Enter area"
            return
        }

        val digits = phone.filter {
            it.isDigit()
        }

        if (
            digits.length !in 9..13 ||
            !Patterns.PHONE.matcher(phone).matches()
        ) {
            edtPhone.error = "Enter a valid phone number"
            return
        }

        if (
            email.isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            edtEmail.error = "Enter a valid email address"
            return
        }

        busy = true
        updateButtons()

        if (isAddMode) {

            db.collection("hospitals")
                .get(Source.SERVER)
                .addOnSuccessListener { snapshot ->

                    val duplicate = snapshot.documents.any {
                        it.getString("name")
                            ?.trim()
                            ?.equals(name, true) == true
                    }

                    if (duplicate) {
                        busy = false
                        updateButtons()

                        edtName.error =
                            "This hospital already exists"

                        return@addOnSuccessListener
                    }

                    val reference = db.collection("hospitals")
                        .document()

                    val newHospitalId =
                        "HOS-${reference.id.take(8).uppercase()}"

                    val bloodStock = mapOf(
                        "APlus" to 0,
                        "AMinus" to 0,
                        "BPlus" to 0,
                        "BMinus" to 0,
                        "ABPlus" to 0,
                        "ABMinus" to 0,
                        "OPlus" to 0,
                        "OMinus" to 0
                    )

                    val fields = hashMapOf<String, Any>(
                        "hospitalId" to newHospitalId,
                        "name" to name,
                        "district" to district,
                        "area" to area,
                        "phone" to phone,
                        "email" to email,
                        "status" to "Active",
                        "acceptingDonors" to true,
                        "bloodStock" to bloodStock,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )

                    reference.set(fields)
                        .addOnSuccessListener {
                            showMessage(
                                "Hospital saved to Firebase"
                            )
                            finish()
                        }
                        .addOnFailureListener { error ->
                            saveFailed(error)
                        }
                }
                .addOnFailureListener { error ->
                    saveFailed(error)
                }

        } else {

            val id = documentId ?: return

            db.collection("hospitals")
                .document(id)
                .update(
                    mapOf(
                        "district" to district,
                        "area" to area,
                        "phone" to phone,
                        "email" to email,
                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .addOnSuccessListener {
                    showMessage(
                        "Hospital changes saved to Firebase"
                    )
                    finish()
                }
                .addOnFailureListener { error ->
                    saveFailed(error)
                }
        }
    }

    private fun confirmStatusChange() {

        if (busy || !loaded || isAddMode) return

        val nextStatus =
            if (currentStatus == "Active") {
                "Inactive"
            } else {
                "Active"
            }

        AlertDialog.Builder(this)
            .setTitle("Change hospital status")
            .setMessage(
                "Change this hospital to $nextStatus?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { _, _ ->

                val id = documentId
                    ?: return@setPositiveButton

                busy = true
                updateButtons()

                db.collection("hospitals")
                    .document(id)
                    .update(
                        mapOf(
                            "status" to nextStatus,
                            "acceptingDonors" to
                                    (nextStatus == "Active"),
                            "updatedAt" to
                                    FieldValue.serverTimestamp()
                        )
                    )
                    .addOnSuccessListener {
                        currentStatus = nextStatus
                        busy = false

                        updateButtons()

                        showMessage(
                            "Hospital status changed to $nextStatus"
                        )
                    }
                    .addOnFailureListener { error ->
                        saveFailed(error)
                    }
            }
            .show()
    }

    private fun saveFailed(error: Exception) {
        busy = false
        updateButtons()

        showMessage(
            "Firebase save failed: ${error.localizedMessage}"
        )
    }

    private fun updateButtons() {
        if (!::btnSave.isInitialized) return

        btnSave.isEnabled = loaded && !busy

        btnStatus.isEnabled =
            loaded && !busy && !isAddMode

        txtStatus.text =
            if (busy) {
                "Working..."
            } else {
                "Status: $currentStatus"
            }

        btnStatus.text =
            if (currentStatus == "Active") {
                "Deactivate Hospital"
            } else {
                "Activate Hospital"
            }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
