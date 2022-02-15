package com.example.testmed.profile

import com.example.testmed.model.PatientData

sealed class PatientDataResult {
    object Loading : PatientDataResult()
    data class Error(val errorMessage: String) : PatientDataResult()
    data class Success(val data: PatientData) : PatientDataResult()
    object NavigateToLogin : PatientDataResult()
}
