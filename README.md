# BloodDononerApp

### Blood Donor–Recipient Matching and Emergency Blood Request Management System

**IT3060 – Human Computer Interaction | Milestone 03**  
**Group ID:** KANDY_UNI_Group12  
**Institution:** Sri Lanka Institute of Information Technology (SLIIT)

---

## Project Overview

BloodDononerApp is a native Android application developed to simplify blood donation management, donor–recipient matching, hospital blood inventory monitoring, and emergency blood request coordination.

The system provides dedicated interfaces for regular users, hospital staff, and administrators.

The main objective is to help users find blood donors, check blood availability, manage blood donation activities, and coordinate urgent requests through a centralized mobile platform.

## Key Features

### User Module

- User registration and secure login
- User profile creation and management
- Blood donor search and matching
- Blood bank availability monitoring
- Blood group and location filtering
- Emergency blood request creation
- Donation appointments and history
- Donor availability management
- Notifications
- Help & Support

### Staff Module

- Staff Dashboard
- Appointment Management
- Appointment Details and Donor Check-in
- Blood Inventory Management
- Donor Matching
- Donor Records
- Donor Screening
- Emergency Request Management
- Staff Notifications
- Staff Profile Management
- Donation Recording
- Emergency Request Details

### Admin Module

- Admin Dashboard
- User Management
- Staff Management
- Hospital Management
- Blood Inventory Monitoring
- Emergency Request Management
- Donation Management
- Reports and Notifications
- Account Activation and Suspension

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Android application development |
| XML | User interface design |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Firebase Firestore | Cloud database |
| Material Design | UI components |
| Figma | UI/UX design and prototyping |
| Git & GitHub | Version control and collaboration |
| Gradle | Android build management |

## System Architecture

The application uses an Android Activity-based architecture.

- **Frontend:** Kotlin and Android XML layouts
- **Authentication:** Firebase Authentication
- **Database:** Firebase Cloud Firestore
- **Navigation:** Role-based Android Activities
- **User Roles:** USER, STAFF, ADMIN

Each role provides specialized screens and operations according to its responsibilities.

---

## Group Members and Contributions

The following table combines the user-interface workload documented in the Milestone 03 report with the team's updated Staff and Admin responsibilities.

| Student ID | Member Name | Assigned Responsibilities |
|---|---|---|
| IT23571266 | De Silva W.A.P.M | Authentication, onboarding, login, registration, profile management, privacy and settings |
| IT23772540 | R.M.A.D. Rathnayake | Donor search, donor matching, donor details, hospital-related user screens and Admin Module |
| IT23759398 | N.T.I.N. Karunarathna | Emergency request creation, request feed, request details, request history and request status |
| IT23536302 | R.M.A.D. Rathnayaka | Donor and blood availability, notifications, donation history, Staff Module, Blood Bank Availability and Help & Support |

**Contribution note:** The original Milestone 03 report documents the user-interface allocation. The individual Staff and Admin role assignments shown here reflect the team's updated work distribution.

---

## Staff Module – IT23536302

**Developer:** R.M.A.D. Rathnayaka  
**Student ID:** IT23536302

The Staff Module focuses on hospital staff operations and blood donation management.

### Implemented Screen Areas

| Screen | GitHub Branch |
|---|---|
| Staff Dashboard | `staff-dashboard` |
| Staff Appointments | `staff-appointments` |
| Staff Appointment Details | `staff-appointment-details` |
| Staff Blood Inventory | `staff-blood-inventory` |
| Staff Donor Matching | `staff-donor-matching` |
| Staff Donor Records | `staff-donor-records` |
| Staff Donor Screening | `staff-donor-screening` |
| Staff Emergency Requests | `staff-emergency-requests` |
| Staff Notifications | `staff-notifications` |
| Staff Profile | `staff-profile` |
| Staff Record Donation | `staff-record-donation` |
| Staff Request Details | `staff-request-details` |

### Additional User Module Contributions

| Screen | GitHub Branch |
|---|---|
| Blood Bank Availability | `user-blood-bank-availability` |
| Help & Support | `user-help-support` |

The Staff Module includes interfaces for managing emergency blood requests, blood inventory, donor screening, donation appointments, and related hospital activities.

The Blood Bank Availability screen provides users with blood stock information from hospital records, while Help & Support provides guidance and information related to blood donation.

---

## Admin Module – IT23772540

**Developer:** R.M.A.D. Rathnayake  
**Student ID:** IT23772540

The Admin Module focuses on administrative oversight and management of the blood donation system.

### Main Responsibilities

- Manage user accounts and account statuses
- Manage hospital staff accounts
- Maintain hospital information
- Monitor emergency blood requests
- Review blood inventory information
- Manage donation records
- Access administrative reports
- View administrative notifications

The Admin Module is maintained separately from the Staff Module to support clear responsibility allocation.

---

## Project File Structure

```text
BloodDononerApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/blooddononerapp/
│           │       └── Kotlin Activity and Adapter files
│           ├── res/
│           │   ├── layout/
│           │   │   └── XML screen layouts
│           │   ├── drawable/
│           │   │   └── Icons and background resources
│           │   └── values/
│           │       └── App resources
│           └── AndroidManifest.xml
├── Gradle configuration files
└── README.md
```

This is the intended complete Android project structure. Individual feature branches may contain only the files relevant to their assigned screens.

---

## GitHub Branch Organization

Development contributions are organized using feature-specific branches.

Each branch may contain:

- Kotlin Activity files
- Supporting Kotlin classes and adapters
- XML layout files
- Drawable XML resources
- Feature documentation

Branches are reviewed and merged into the main project through GitHub Pull Requests.

This structure makes it easier to identify individual screen implementations and review contributions.

---

## Setup and Installation

After the complete Android project source and Gradle configuration have been committed:

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Install the required Android SDK.
4. Configure the Firebase project.
5. Add the appropriate `google-services.json` file to the Android app module.
6. Enable the required Firebase Authentication providers.
7. Configure Firestore data and security rules.
8. Synchronize the Gradle dependencies.
9. Run the application using an Android emulator or physical device.
10. Test role-based login and the relevant application functions.

**Minimum Android SDK:** 24  
**Compile SDK:** 36  
**Target SDK:** 36

A fully runnable build requires the complete Android project, its dependencies, and a correctly configured Firebase environment.

---

## Project Objectives

- Improve access to blood donor information.
- Reduce difficulties in finding available donors.
- Support emergency blood request coordination.
- Provide hospital blood stock information.
- Improve blood donation appointment management.
- Support hospital staff through dedicated management interfaces.
- Provide administrators with centralized management tools.
- Deliver a user-friendly and accessible Android interface.

---

## Academic Information

**Module:** IT3060 – Human Computer Interaction  
**Assessment:** Milestone 03  
**Project:** Blood Donor–Recipient Matching and Emergency Blood Request App  
**Group:** KANDY_UNI_Group12  
**Institution:** Sri Lanka Institute of Information Technology

## Project Repository

https://github.com/Amila1103/BloodDononerApp

## Project Status

Academic group project with individual development branches for user, staff, and administrative features.

Feature integration and complete-project verification should be performed after merging all required branches.

---

**Developed by KANDY_UNI_Group12 – SLIIT**

*BloodDononerApp – Connecting blood donors, recipients, and healthcare staff through technology.*
