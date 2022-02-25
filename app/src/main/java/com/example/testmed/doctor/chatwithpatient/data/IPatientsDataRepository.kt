package com.example.testmed.doctor.chatwithpatient.data

import com.example.testmed.doctor.chatwithpatient.MessagingResult
import kotlinx.coroutines.flow.MutableStateFlow

interface IPatientsDataRepository {
    suspend fun getDoctorsData(idPatient: String): PatientDataResult
//    suspend fun getMessages(idPatient: String) : MessagingResult
}