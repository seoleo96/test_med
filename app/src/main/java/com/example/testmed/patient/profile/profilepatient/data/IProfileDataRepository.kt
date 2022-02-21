package com.example.testmed.patient.profile.profilepatient.data

import com.example.testmed.patient.profile.profilepatient.PatientDataResult
import kotlinx.coroutines.flow.Flow

interface IProfileDataRepository {
    fun fetchPatientData(): Flow<PatientDataResult>
    suspend fun signOut()
}