package com.example.testmed.profile.data

import com.example.testmed.AUTH
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.model.PatientData
import com.example.testmed.profile.PatientDataResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileDataRepository : IProfileDataRepository {
    private val mutableStateFlow = MutableStateFlow<PatientDataResult>(PatientDataResult.Loading)
    override fun fetchPatientData(): Flow<PatientDataResult> {
        DB.reference
            .child("patients")
            .child(UID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(PatientData::class.java)
                        if (data != null) {
                            mutableStateFlow.value = PatientDataResult.Success(data)
                        } else {
                            mutableStateFlow.value = PatientDataResult.Error("Данных нет.")
                        }
                    } else {
                        mutableStateFlow.value = PatientDataResult.NavigateToLogin
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return mutableStateFlow
    }

    override suspend fun signOut() {
        AUTH.signOut()
    }
}