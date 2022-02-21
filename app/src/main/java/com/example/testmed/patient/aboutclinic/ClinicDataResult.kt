package com.example.testmed.patient.aboutclinic

import com.example.testmed.model.ClinicData

sealed class ClinicDataResult {
    object Loading : ClinicDataResult()
    data class Error(val errorMessage: String) : ClinicDataResult()
    data class Success(val data: ClinicData) : ClinicDataResult()
}
