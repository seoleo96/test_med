package com.example.testmed.doctor.chatwithpatient.data

import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.PatientData

sealed class PatientDataResult {
    object NotExist : PatientDataResult()
    data class Success(val data: PatientData) : PatientDataResult()
}
