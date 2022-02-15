package com.example.testmed.aboutclinic.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.testmed.aboutclinic.ClinicDataResult
import com.example.testmed.aboutclinic.data.IClinicDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class ClinicDataViewModel(
    private val iClinicDataRepository: IClinicDataRepository
) : ViewModel() {
    val clinicDataLiveData : LiveData<ClinicDataResult> = liveData(Dispatchers.IO) {
        emit(ClinicDataResult.Loading)
           iClinicDataRepository.fetchClinicData().collect {
               kotlinx.coroutines.delay(500)
               emit(it)
           }
    }
}