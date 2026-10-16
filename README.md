# 🩸 BloodDononerApp

### Blood Donor–Recipient Matching & Emergency Blood Request Management System

**IT3060 – Human Computer Interaction | Milestone 03**

**Group ID:** KANDY_UNI_Group12  
**University:** Sri Lanka Institute of Information Technology (SLIIT)

---

## 📱 Project Overview

BloodDononerApp is an Android-based Blood Donation Management System designed to connect blood donors, recipients, hospitals, and blood bank staff.

The system aims to simplify donor searching, emergency blood requests, donation appointments, blood inventory management, and administrative operations.

It provides three main user roles:

- **USER** – Blood donors and recipients
- **STAFF** – Hospital and blood bank staff
- **ADMIN** – System administrators

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Android Application Development |
| XML | User Interface Design |
| Android Studio | Development Environment |
| Firebase Authentication | Account Authentication |
| Firebase Firestore | Cloud Database |
| Material Design | UI Components |
| Figma | UI/UX Design and Prototyping |
| GitHub | Version Control and Collaboration |
| Gradle | Android Build Management |

---

# 👥 Group Members & Individual Contributions

## 👨‍💻 Member 01 – IT23571266

**Name:** De Silva W.A.P.M  
**Student ID:** IT23571266

### Assigned Module: Authentication & User Profile Management

Responsible for designing and implementing the authentication, onboarding, and user profile interfaces.

**Assigned Screens:**

1. Splash Screen
2. Onboarding Screens
3. User Registration
4. User Login
5. Forgot Password
6. Profile Setup
7. My Profile
8. Edit Profile
9. Privacy Settings

**Main Responsibilities:**

- Develop user-friendly authentication interfaces.
- Implement user registration and login workflows.
- Manage profile creation and editing.
- Provide password recovery functionality.
- Design privacy and account settings screens.
- Support Firebase-based authentication and profile management.

---

## 👨‍💻 Member 02 – IT23772540

**Name:** R.M.A.D. Rathnayake  
**Student ID:** IT23772540

### Assigned Modules: Donor Search & Admin Management

Responsible for donor discovery and matching interfaces, along with the Admin Role.

### Part A – Donor Search & Matching

**Assigned Screens:**

1. User Home
2. Find Donor
3. Donor Search Results
4. Donor Details
5. Hospital Information
6. Donor Matching and Contact Options

**Main Responsibilities:**

- Implement donor searching and filtering.
- Support blood group and location-based donor discovery.
- Display matching donor information.
- Provide donor contact functionality.
- Develop hospital-related user interfaces.

### Part B – Admin Role

**Assigned Admin Areas:**

1. Admin Dashboard
2. User Management
3. Staff Management
4. Hospital Management
5. Blood Inventory Monitoring
6. Emergency Request Management
7. Donation Management
8. Admin Notifications
9. Reports and Administrative Overview
10. Account Activation and Suspension

**Main Responsibilities:**

- Develop and maintain administrator interfaces.
- Manage user and staff accounts.
- Support hospital management.
- Monitor emergency blood requests.
- Provide administrative inventory and donation views.
- Support account status and management operations.

---

## 👨‍💻 Member 03 – IT23759398

**Name:** N.T.I.N. Karunarathna  
**Student ID:** IT23759398

### Assigned Module: Emergency Blood Request Management

Responsible for the emergency blood request interfaces within the User Role.

**Assigned Screens:**

1. Create Emergency Request
2. Emergency Requests Feed
3. Emergency Request Details
4. My Requests
5. Request Status

**Main Responsibilities:**

- Design emergency blood request forms.
- Implement request submission workflows.
- Display emergency blood request information.
- Provide request history and status tracking.
- Support request cancellation where applicable.
- Improve request navigation and user feedback.

---

## 👨‍💻 Member 04 – IT23536302

**Name:** R.M.A.D. Rathnayaka  
**Student ID:** IT23536302

### Assigned Modules: Staff Role & User Blood Management

Responsible for the Staff Role screens and assigned blood management features within the User Role.

### Part A – Staff Role

**Assigned Screens and GitHub Branches:**

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

**Main Responsibilities:**

- Develop the Staff Dashboard.
- Implement hospital blood inventory management.
- Manage blood donation appointments.
- Support donor screening and matching.
- Record completed blood donations.
- Display and manage donor records.
- Support emergency request verification and status updates.
- Implement staff notification and profile interfaces.

### Part B – User Role

**Assigned Features:**

1. Donor Availability
2. Blood Availability
3. User Notifications
4. Donation History
5. Blood Bank Availability
6. Help & Support

**Additional GitHub Branches:**

| Screen | GitHub Branch |
|---|---|
| Blood Bank Availability | `user-blood-bank-availability` |
| Help & Support | `user-help-support` |

**Main Responsibilities:**

- Display hospital blood bank availability.
- Support searching and filtering available blood stock.
- Present blood group and availability information.
- Provide user guidance and frequently asked questions.
- Support blood donation information and Help & Support interfaces.
- Contribute to donor availability, notifications, and donation history features.

---

# 🔐 System Roles

### User Role
Provides access to donor search, blood availability, donation history, emergency requests, notifications, and account management.

### Staff Role
Provides hospital staff with appointment management, donor screening, blood inventory management, donation recording, and emergency request coordination.

### Admin Role
Provides system management interfaces for users, staff, hospitals, emergency requests, donations, inventory, and administrative reports.

---

# 📁 GitHub Branch Organization

The project uses individual feature branches to organize development contributions.

Each screen-specific branch may include:

- Kotlin Activity files
- Kotlin Adapter files
- XML Layout files
- Drawable XML resources
- Screen-specific documentation

The `main` branch is intended to contain the integrated project following review and merging of feature contributions.

---

# 🚀 Project Objectives

1. Improve access to blood donor information.
2. Simplify donor–recipient matching.
3. Improve emergency blood request coordination.
4. Provide blood bank and hospital stock visibility.
5. Support organized donation appointment management.
6. Provide efficient hospital staff workflows.
7. Improve administrative oversight.
8. Deliver a simple and accessible Android experience.

---

# ⚙️ Setup & Installation

To run the complete application:

1. Clone or download the integrated repository.
2. Open the Android project in Android Studio.
3. Install the required Android SDK.
4. Configure Firebase Authentication and Cloud Firestore.
5. Add the appropriate Firebase configuration file.
6. Synchronize Gradle dependencies.
7. Build and run the application using an Android emulator or physical Android device.
8. Test the User, Staff, and Admin workflows.

**Note:** A runnable build requires the complete Android project, its dependencies, and the correct backend configuration. Individual screen branches are not necessarily standalone applications.

---

# 📚 Academic Information

**Module:** IT3060 – Human Computer Interaction  
**Assessment:** Milestone 03  
**Group:** KANDY_UNI_Group12  
**Institution:** Sri Lanka Institute of Information Technology

### GitHub Repository

https://github.com/Amila1103/BloodDononerApp

---

## 👥 Development Team

- **IT23571266** – De Silva W.A.P.M
- **IT23772540** – R.M.A.D. Rathnayake
- **IT23759398** – N.T.I.N. Karunarathna
- **IT23536302** – R.M.A.D. Rathnayaka

*The original user-interface workload follows the group Milestone 03 allocation. Staff and Admin individual responsibilities reflect the team's updated allocation.*

---

**Developed by KANDY_UNI_Group12 – SLIIT**

*BloodDononerApp – Connecting Donors, Recipients, and Healthcare Professionals.*
