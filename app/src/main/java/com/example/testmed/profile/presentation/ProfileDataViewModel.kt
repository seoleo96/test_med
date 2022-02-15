package com.example.testmed.profile.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.testmed.profile.PatientDataResult
import com.example.testmed.profile.data.IProfileDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileDataViewModel(
    private val profileDataRepository: IProfileDataRepository,
) : ViewModel() {
    val patientDataLiveData: LiveData<PatientDataResult> = liveData(Dispatchers.IO) {
        emit(PatientDataResult.Loading)
        profileDataRepository.fetchPatientData().collect {
            emit(it)
        }
    }

    fun signOut(){
        viewModelScope.launch(Dispatchers.IO) {
            profileDataRepository.signOut()
        }
    }
}