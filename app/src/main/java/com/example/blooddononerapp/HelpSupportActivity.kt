package com.example.blooddononerapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var cardAbout: MaterialCardView
    private lateinit var cardEligibility: MaterialCardView
    private lateinit var cardDonationProcess: MaterialCardView
    private lateinit var cardBloodGroup: MaterialCardView
    private lateinit var cardEmergencyRequest: MaterialCardView
    private lateinit var cardFindHelp: MaterialCardView
    private lateinit var cardFaq: MaterialCardView
    private lateinit var cardPrivacy: MaterialCardView
    private lateinit var cardContact: MaterialCardView

    private lateinit var sectionLearn: LinearLayout
    private lateinit var sectionRequest: LinearLayout
    private lateinit var sectionHelp: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_help_support)

        val btnBack =
            findViewById<TextView>(R.id.btnBack)

        val searchHelp =
            findViewById<EditText>(R.id.searchHelp)

        val btnEmergencyHelp =
            findViewById<MaterialButton>(R.id.btnEmergencyHelp)

        sectionLearn =
            findViewById(R.id.sectionLearn)

        sectionRequest =
            findViewById(R.id.sectionRequest)

        sectionHelp =
            findViewById(R.id.sectionHelp)

        cardAbout =
            findViewById(R.id.cardAbout)

        cardEligibility =
            findViewById(R.id.cardEligibility)

        cardDonationProcess =
            findViewById(R.id.cardDonationProcess)

        cardBloodGroup =
            findViewById(R.id.cardBloodGroup)

        cardEmergencyRequest =
            findViewById(R.id.cardEmergencyRequest)

        cardFindHelp =
            findViewById(R.id.cardFindHelp)

        cardFaq =
            findViewById(R.id.cardFaq)

        cardPrivacy =
            findViewById(R.id.cardPrivacy)

        cardContact =
            findViewById(R.id.cardContact)


        // BACK
        btnBack.setOnClickListener {
            finish()
        }


        // ABOUT BLOOD DONATION
        cardAbout.setOnClickListener {

            showHelpDialog(
                "About Blood Donation",
                """
Blood donation is the voluntary process of giving blood so it can be used when patients need blood or blood components.

Why blood donation matters

• Donated blood can support patients during emergencies, surgeries and other medical treatment.

• Blood banks collect, test, process and store donated blood.

• Different patients may require different blood groups or blood components.

Before donating

• Follow the instructions provided by the donation center.

• Provide accurate information during donor screening.

• Final donor eligibility is determined by qualified medical staff at the donation center.
                """.trimIndent()
            )
        }


        // ELIGIBILITY
        cardEligibility.setOnClickListener {

            showHelpDialog(
                "Eligibility Criteria",
                """
Donation eligibility must be confirmed by qualified medical staff.

Before a donation, the center may ask questions about your health and donation history and may perform basic checks.

You should provide accurate information during screening.

If you are unsure whether you can donate, contact the selected blood donation center before your appointment.

The eligibility information shown in this app is only preliminary guidance. Final approval is made by medical staff.
                """.trimIndent()
            )
        }


        // DONATION PROCESS
        cardDonationProcess.setOnClickListener {

            showHelpDialog(
                "Donation Process",
                """
1. Find a blood bank or donation center.

2. Select Donate Blood.

3. Choose your blood group, preferred date and available time.

4. Complete the preliminary eligibility questions.

5. Review and confirm your appointment.

6. Visit the selected donation center at the scheduled time.

7. Medical staff will perform the required checks.

8. If you are eligible, the donation process can proceed.

Your appointment can be viewed from My Requests → Donations.
                """.trimIndent()
            )
        }


        // BLOOD GROUP GUIDE
        cardBloodGroup.setOnClickListener {

            showHelpDialog(
                "Blood Group Guide",
                """
The main blood groups used in the app are:

A+
A−
B+
B−
AB+
AB−
O+
O−

Your blood group is important when searching for donors, requesting blood and arranging donations.

Do not guess your blood group.

If you are unsure about your blood group, confirm it using an appropriate medical test or healthcare record.
                """.trimIndent()
            )
        }


        // EMERGENCY REQUEST
        cardEmergencyRequest.setOnClickListener {

            showHelpDialog(
                "Emergency Blood Requests",
                """
You can create an emergency blood request from the Home screen.

When creating a request:

• Select the required blood group.

• Select the number of units.

• Enter the patient's area.

• Select the hospital or blood bank.

• Enter the required date and time.

• Provide valid contact details.

• Select the urgency level.

After submitting the request, you can view its status from:

My Requests → Blood Requests

For a medical emergency requiring immediate treatment, contact an appropriate hospital or emergency medical service directly.
                """.trimIndent()
            )
        }


        // FIND DONORS / BLOOD BANKS
        cardFindHelp.setOnClickListener {

            showHelpDialog(
                "Finding Donors & Blood Banks",
                """
Find Donor

Use Find Donor to search for suitable donors based on the available search criteria in the app.

Blood Bank Availability

Use the Blood Bank section to browse blood banks and donation centers.

You can use the available filters to narrow the displayed information.

Availability information should be confirmed with the relevant blood bank or hospital when necessary.

For urgent medical needs, contact the relevant healthcare provider directly.
                """.trimIndent()
            )
        }


        // FAQ
        cardFaq.setOnClickListener {

            showHelpDialog(
                "Frequently Asked Questions",
                """
How do I request blood?

Open Emergency Blood Request from Home, complete the required information and submit the request.


Where can I see my request?

Open My Requests and select Blood Requests.


Where can I see my donation appointment?

Open My Requests and select Donations.


Can I cancel a request?

Active requests can be cancelled where the Cancel option is available.


Can I cancel a donation appointment?

Open My Requests → Donations and use the available cancellation option.


How do I find a donor?

Select Find Donor from the Home screen.


How do I find a donation center?

Open Blood Bank Availability from the Home screen.
                """.trimIndent()
            )
        }


        // PRIVACY
        cardPrivacy.setOnClickListener {

            showHelpDialog(
                "Privacy & Safety",
                """
Only provide information that is necessary for your blood request or donation activity.

Contact details used for blood requests should be kept accurate.

Avoid sharing unnecessary personal or sensitive information in request descriptions.

Location and contact information should only be used for legitimate donation or blood-request coordination.

Do not rely only on the app during an immediate medical emergency. Contact an appropriate healthcare provider or emergency service.
                """.trimIndent()
            )
        }


        // CONTACT
        cardContact.setOnClickListener {

            showHelpDialog(
                "Contact Us",
                """
Need help using the app?

You can contact the support team for assistance with:

• Account and profile issues

• Blood request features

• Donor search

• Donation appointments

• Blood bank information

• Technical problems

Support Email
support@blooddonorapp.com

Important:
The support team does not provide medical diagnosis or emergency medical treatment.
                """.trimIndent()
            )
        }


        // EMERGENCY HELP
        btnEmergencyHelp.setOnClickListener {

            showEmergencyDialog()
        }


        // SEARCH
        searchHelp.addTextChangedListener {

            filterTopics(
                it.toString()
            )
        }
    }


    private fun filterTopics(query: String) {

        val text =
            query.trim().lowercase()

        if (text.isEmpty()) {

            cardAbout.visibility =
                android.view.View.VISIBLE

            cardEligibility.visibility =
                android.view.View.VISIBLE

            cardDonationProcess.visibility =
                android.view.View.VISIBLE

            cardBloodGroup.visibility =
                android.view.View.VISIBLE

            cardEmergencyRequest.visibility =
                android.view.View.VISIBLE

            cardFindHelp.visibility =
                android.view.View.VISIBLE

            cardFaq.visibility =
                android.view.View.VISIBLE

            cardPrivacy.visibility =
                android.view.View.VISIBLE

            cardContact.visibility =
                android.view.View.VISIBLE

            sectionLearn.visibility =
                android.view.View.VISIBLE

            sectionRequest.visibility =
                android.view.View.VISIBLE

            sectionHelp.visibility =
                android.view.View.VISIBLE

            return
        }


        setCardVisibility(
            cardAbout,
            text,
            "about blood donation blood basics learn"
        )

        setCardVisibility(
            cardEligibility,
            text,
            "eligibility eligible donate donor criteria"
        )

        setCardVisibility(
            cardDonationProcess,
            text,
            "donation process appointment donate steps"
        )

        setCardVisibility(
            cardBloodGroup,
            text,
            "blood group a b ab o positive negative"
        )

        setCardVisibility(
            cardEmergencyRequest,
            text,
            "emergency blood request patient recipient urgent"
        )

        setCardVisibility(
            cardFindHelp,
            text,
            "find donor donors blood bank hospital search"
        )

        setCardVisibility(
            cardFaq,
            text,
            "faq frequently asked questions help"
        )

        setCardVisibility(
            cardPrivacy,
            text,
            "privacy safety information data"
        )

        setCardVisibility(
            cardContact,
            text,
            "contact support help email"
        )


        sectionLearn.visibility =
            if (
                cardAbout.visibility == android.view.View.VISIBLE ||
                cardEligibility.visibility == android.view.View.VISIBLE ||
                cardDonationProcess.visibility == android.view.View.VISIBLE ||
                cardBloodGroup.visibility == android.view.View.VISIBLE
            )
                android.view.View.VISIBLE
            else
                android.view.View.GONE


        sectionRequest.visibility =
            if (
                cardEmergencyRequest.visibility == android.view.View.VISIBLE ||
                cardFindHelp.visibility == android.view.View.VISIBLE
            )
                android.view.View.VISIBLE
            else
                android.view.View.GONE


        sectionHelp.visibility =
            if (
                cardFaq.visibility == android.view.View.VISIBLE ||
                cardPrivacy.visibility == android.view.View.VISIBLE ||
                cardContact.visibility == android.view.View.VISIBLE
            )
                android.view.View.VISIBLE
            else
                android.view.View.GONE
    }


    private fun setCardVisibility(
        card: MaterialCardView,
        query: String,
        keywords: String
    ) {

        card.visibility =
            if (keywords.contains(query))
                android.view.View.VISIBLE
            else
                android.view.View.GONE
    }


    private fun showHelpDialog(
        title: String,
        content: String
    ) {

        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        dialog.setContentView(
            R.layout.dialog_help_details
        )

        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )


        val txtTitle =
            dialog.findViewById<TextView>(
                R.id.txtHelpDialogTitle
            )

        val txtContent =
            dialog.findViewById<TextView>(
                R.id.txtHelpDialogContent
            )

        val btnClose =
            dialog.findViewById<MaterialButton>(
                R.id.btnCloseHelp
            )


        txtTitle.text =
            title

        txtContent.text =
            content


        btnClose.setOnClickListener {

            dialog.dismiss()
        }


        dialog.show()


        dialog.window?.setLayout(
            (
                    resources
                        .displayMetrics
                        .widthPixels * 0.90
                    ).toInt(),

            (
                    resources
                        .displayMetrics
                        .heightPixels * 0.80
                    ).toInt()
        )


        dialog.window?.addFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )


        val params =
            dialog.window?.attributes

        params?.dimAmount =
            0.55f

        dialog.window?.attributes =
            params
    }


    private fun showEmergencyDialog() {

        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        dialog.setContentView(
            R.layout.dialog_help_details
        )

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )


        val title =
            dialog.findViewById<TextView>(
                R.id.txtHelpDialogTitle
            )

        val content =
            dialog.findViewById<TextView>(
                R.id.txtHelpDialogContent
            )

        val close =
            dialog.findViewById<MaterialButton>(
                R.id.btnCloseHelp
            )


        title.text =
            "Emergency Help"

        content.text =
            """
If someone requires immediate medical attention, contact an appropriate hospital or emergency medical service.

The Blood Donor app can help with donor searches and blood requests, but it should not replace professional emergency medical care.

For an urgent blood requirement:

1. Contact the treating hospital.
2. Confirm the required blood group and requirement.
3. Use Emergency Blood Request to help coordinate the request.
4. Keep the provided contact number available.
            """.trimIndent()


        close.setOnClickListener {

            dialog.dismiss()
        }


        dialog.show()


        dialog.window?.setLayout(
            (
                    resources
                        .displayMetrics
                        .widthPixels * 0.90
                    ).toInt(),

            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}