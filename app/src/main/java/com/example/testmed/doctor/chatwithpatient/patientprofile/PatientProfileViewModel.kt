package com.example.testmed.doctor.chatwithpatient.patientprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.model.PatientData
import com.example.testmed.patient.profile.profilepatient.PatientDataResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PatientProfileViewModel : ViewModel() {

    private val _profileLiveData = MutableLiveData<PatientDataResult>(PatientDataResult.Loading)
    val profileLiveData = _profileLiveData

    fun fetchPatientData(idPatient : String){
        DB.reference
            .child("patients")
            .child(idPatient)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(PatientData::class.java)
                        if (data != null) {
                            _profileLiveData.value = PatientDataResult.Success(data)
                        } else {
                            _profileLiveData.value = PatientDataResult.Error("Данных нет.")
                        }
                    } else {
                        _profileLiveData.value = PatientDataResult.NavigateToLogin
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }
}