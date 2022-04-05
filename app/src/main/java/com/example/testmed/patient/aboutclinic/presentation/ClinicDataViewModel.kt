package com.example.testmed.patient.aboutclinic.presentation

import androidx.lifecycle.*
import com.example.testmed.model.AllClinicData
import com.example.testmed.patient.aboutclinic.ClinicDataResult
import com.example.testmed.patient.aboutclinic.data.IClinicDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClinicDataViewModel(
    private val iClinicDataRepository: IClinicDataRepository
) : ViewModel() {
    private val _clinicDataLiveData : MutableLiveData<ClinicDataResult> = MutableLiveData(ClinicDataResult.Loading)
    val clinicDataLiveData : LiveData<ClinicDataResult> = _clinicDataLiveData

    fun getClinicDate(idClinic : String){
        viewModelScope.launch(Dispatchers.IO){
            iClinicDataRepository.fetchClinicData(idClinic).collect {
                withContext(Dispatchers.Main){
                    _clinicDataLiveData.value =it
                }
            }
        }
    }

}