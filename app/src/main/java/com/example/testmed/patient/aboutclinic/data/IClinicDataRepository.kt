package com.example.testmed.patient.aboutclinic.data

import com.example.testmed.patient.aboutclinic.ClinicDataResult
import kotlinx.coroutines.flow.Flow

interface IClinicDataRepository {
    fun fetchClinicData(idClinic : String) : Flow<ClinicDataResult>
}