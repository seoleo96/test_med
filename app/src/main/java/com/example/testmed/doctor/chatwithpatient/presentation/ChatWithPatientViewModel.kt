package com.example.testmed.doctor.chatwithpatient.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.doctor.chatwithpatient.MessagingResult
import com.example.testmed.doctor.chatwithpatient.data.PatientDataResult
import com.example.testmed.doctor.chatwithpatient.data.PatientsDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatWithPatientViewModel(
    private val patientsDataRepository: PatientsDataRepository,
) : ViewModel() {

    private val _patientDataLiveData = MutableLiveData<PatientDataResult>()
    val patientDataLiveData = _patientDataLiveData

    private val _messagesLiveData = MutableLiveData<MessagingResult>()
    val messagesLiveData: LiveData<MessagingResult> = _messagesLiveData


    fun getPatientData(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val flowData: PatientDataResult = patientsDataRepository.getDoctorsData(id)
            withContext(Dispatchers.Main) {
                _patientDataLiveData.value = flowData
            }
        }
    }

    fun getMessage(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val flowData: MessagingResult = patientsDataRepository.getMessages(id)
            withContext(Dispatchers.Main){
                _messagesLiveData.value = flowData
            }
        }
    }

}