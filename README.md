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
| Kotlin | Android Application Development |
| XML | User Interface Design |
| Android Studio | Development Environment |
| Firebase Authentication | Account Authentication |
| Firebase Firestore | Cloud Database |
| Material Design | UI Components |
| Figma | UI/UX Design and Prototyping |
| Git & GitHub | Version Control and Collaboration |
| Gradle | Android Build Management |

---

## System Architecture

The application uses an Android Activity-based architecture.

- **Frontend:** Kotlin and Android XML layouts
- **Authentication:** Firebase Authentication
- **Database:** Firebase Cloud Firestore
- **Navigation:** Role-based Android Activities
- **User Roles:** USER, STAFF, ADMIN

Each role provides specialized screens and operations according to its responsibilities.

---

# Group Members and Individual Contributions

The following work distribution is based on the Milestone 03 group report and the team's updated Staff and Admin module assignments.

| Student ID | Member Name | Assigned Responsibilities |
|---|---|---|
| IT23571266 | De Silva W.A.P.M | Authentication, Onboarding and User Profile Management |
| IT23772540 | R.M.A.D. Rathnayake | Donor Search, Matching and Admin Module |
| IT23759398 | N.T.I.N. Karunarathna | Emergency Blood Request Management |
| IT23536302 | R.M.A.D. Rathnayaka | Staff Module, Donor and Blood Availability, Notifications, Donation History and Help & Support |

---

## Member 01 – Authentication and User Profile Management

**Name:** De Silva W.A.P.M  
**Student ID:** IT23571266

### Assigned Screens

1. Splash Screen
2. Onboarding Screens
3. Login Screen
4. Registration Screen
5. Forgot Password
6. Profile Setup
7. My Profile
8. Edit Profile
9. Privacy Settings

### Main Responsibilities

- Develop user authentication interfaces.
- Implement registration and login workflows.
- Create onboarding and profile setup interfaces.
- Manage user profile information and editing.
- Support password recovery functionality.
- Implement privacy and account settings interfaces.
- Integrate authentication-related operations with Firebase.

---

## Member 02 – Donor Search and Admin Management

**Name:** R.M.A.D. Rathnayake  
**Student ID:** IT23772540

### Part A – Donor Search and Matching

**Assigned Screens**

1. User Home
2. Find Donor
3. Donor Search Results
4. Donor Details
5. Hospital Information
6. Donor Contact and Matching Interfaces

**Main Responsibilities**

- Implement donor searching functionality.
- Support blood group and location-based filtering.
- Display matching donor records.
- Provide donor details and contact actions.
- Develop hospital-related user interfaces.

### Part B – Admin Module

**Assigned Admin Screens and Areas**

1. Admin Dashboard
2. User Management
3. Staff Management
4. Hospital Management
5. Blood Inventory Monitoring
6. Emergency Requests Management
7. Donation Management
8. Admin Notifications
9. Admin Reports
10. Admin Profile and Account Management

**Main Responsibilities**

- Develop administrative management interfaces.
- Manage user and staff account information.
- Support hospital registration and management.
- Monitor blood inventory and emergency requests.
- Manage account activation and suspension.
- Support donation records and administrative reporting.

---

## Member 03 – Emergency Blood Request Management

**Name:** N.T.I.N. Karunarathna  
**Student ID:** IT23759398

### Assigned Screens

1. Create Emergency Request
2. Emergency Requests Feed
3. Emergency Request Details
4. My Requests
5. Request Status

### Main Responsibilities

- Develop emergency blood request interfaces.
- Implement request submission and validation.
- Display emergency blood request information.
- Provide request history and tracking.
- Support request cancellation and status management.
- Improve emergency request navigation and feedback.
- Integrate request-related data with Firebase Firestore.

---

## Member 04 – Staff and Blood Management

**Name:** R.M.A.D. Rathnayaka  
**Student ID:** IT23536302

### Part A – Staff Module

Responsible for hospital staff operations, blood inventory management, donor screening, donation recording, and emergency request coordination.

**Assigned Staff Screens and GitHub Branches**

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

**Main Responsibilities**

- Develop Staff Dashboard and navigation.
- Implement blood donation appointment management.
- Manage hospital blood inventory and stock updates.
- Support donor matching and donor screening workflows.
- Display and manage donor records.
- Record completed blood donations.
- Manage emergency blood requests and status changes.
- Develop staff notifications and profile interfaces.

### Part B – User Module

**Assigned User Features**

1. Donor Availability
2. Blood Availability
3. User Notifications
4. Donation History
5. Blood Bank Availability
6. Help & Support

**Additional GitHub Branches**

| Screen | GitHub Branch |
|---|---|
| Blood Bank Availability | `user-blood-bank-availability` |
| Help & Support | `user-help-support` |

**Main Responsibilities**

- Display hospital blood bank availability.
- Retrieve hospital blood stock information.
- Support blood group searching and filtering.
- Contribute to donor availability management.
- Support user notifications and donation history.
- Develop Help & Support and blood donation guidance interfaces.

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

This represents the intended integrated Android project structure. Individual feature branches may contain only their relevant implementation files.

---

## GitHub Branch Organization

The repository uses feature-specific branches to organize individual development contributions.

Each branch may include:

- Kotlin Activity files
- Kotlin Adapter files
- XML layout files
- Drawable XML resources
- Screen-specific documentation

The `main` branch is intended to contain the integrated project following review and merging.

This approach supports clearer contribution tracking and easier code review.

---

## Setup and Installation

After the complete Android project has been integrated:

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Install the required Android SDK.
4. Configure the Firebase project.
5. Add the appropriate `google-services.json` to the Android app module.
6. Enable Firebase Authentication.
7. Configure Firebase Firestore and security rules.
8. Synchronize Gradle dependencies.
9. Run the application using an Android emulator or physical device.
10. Test the User, Staff, and Admin workflows.

**Minimum Android SDK:** 24  
**Compile SDK:** 36  
**Target SDK:** 36

A runnable build requires the complete Android project and correctly configured Firebase services.

---

## Project Objectives

- Improve access to blood donor information.
- Reduce difficulties in finding available blood donors.
- Simplify donor–recipient matching.
- Support emergency blood request coordination.
- Provide hospital blood stock information.
- Improve donation appointment management.
- Support hospital staff with dedicated management interfaces.
- Provide administrators with centralized management tools.
- Deliver a user-friendly and accessible Android application.

---

## Academic Information

**Module:** IT3060 – Human Computer Interaction  
**Assessment:** Milestone 03  
**Project:** Blood Donor–Recipient Matching and Emergency Blood Request App  
**Group:** KANDY_UNI_Group12  
**Institution:** Sri Lanka Institute of Information Technology

### Project Repository

https://github.com/Amila1103/BloodDononerApp

---

## Project Status

Academic group project organized into individual development branches covering User, Staff, and Admin features.

The original user-interface workload follows the Milestone 03 report. Individual Staff and Admin responsibilities reflect the team's updated allocation.

Complete-project integration and verification should be performed after merging the required development branches.

---

**Developed by KANDY_UNI_Group12 – SLIIT**

*BloodDononerApp – Connecting blood donors, recipients, and healthcare staff through technology.*
