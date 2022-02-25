package com.example.testmed

enum class AppStates(val state:String) {

    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object{
        fun updateStatePatient(appStates: AppStates){
            DB.reference.child("patients").child(UID()).child("state")
                .setValue(appStates.state)
                .addOnSuccessListener { PATIENT_STATUS = appStates.state }
                .addOnFailureListener {  }
        }

//        fun updateStateDoctor(appStates: AppStates){
//            DB.reference.child("doctors").child(UID()).child("state")
//                .setValue(appStates.state)
//                .addOnSuccessListener { DOCTOR = appStates.state }
//                .addOnFailureListener {  }
//        }
    }
}