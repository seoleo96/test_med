package com.example.testmed.profile.data

import com.example.testmed.profile.PatientDataResult
import kotlinx.coroutines.flow.Flow

interface IProfileDataRepository {
    fun fetchPatientData(): Flow<PatientDataResult>
    suspend fun signOut()
}