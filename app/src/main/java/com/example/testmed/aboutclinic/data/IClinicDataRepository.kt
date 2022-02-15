package com.example.testmed.aboutclinic.data

import com.example.testmed.aboutclinic.ClinicDataResult
import kotlinx.coroutines.flow.Flow

interface IClinicDataRepository {
    fun fetchClinicData() : Flow<ClinicDataResult>
}