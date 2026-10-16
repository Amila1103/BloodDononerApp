# Staff Emergency Requests Management

## BloodDononerApp – Staff Module

### Overview
The Staff Emergency Requests screen provides a centralized interface for hospital staff to monitor and review emergency blood requests.

### Key Features
- Retrieve emergency blood requests from Firebase Firestore.
- Load requests associated with the assigned hospital.
- Display requester, hospital, blood group, and urgency information.
- Filter requests by status.
- Support Pending, Verified, Matching, Fulfilled, and Rejected status views.
- Refresh emergency request information.
- Open detailed request information.
- Display request summaries and empty-state messages.

### Technologies Used
- Kotlin
- Android XML
- Firebase Authentication
- Firebase Firestore
- RecyclerView
- Material Design Components

### Main Files
- `StaffEmergencyRequestsActivity.kt` – Request loading and filtering.
- `StaffEmergencyRequestAdapter.kt` – RecyclerView adapter.
- `activity_staff_emergency_requests.xml` – Request list interface.
- `item_staff_emergency_request.xml` – Individual request layout.
- `res/drawable/` – Status badges, icons, and backgrounds.

### GitHub Branch
`staff-emergency-requests`

### Developer
**Amila Rathnayaka**  
Student ID: IT23536302  
Institution: SLIIT

### Repository
https://github.com/Amila1103/BloodDononerApp

---
Developed as part of the BloodDononerApp academic group project.
