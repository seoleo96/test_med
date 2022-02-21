package com.example.testmed.doctor.home.chats

import androidx.lifecycle.*
import com.example.testmed.doctor.home.chats.data.ReceivingPatientsDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: ReceivingPatientsDataRepository,
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.receivingPatientsData()
        }
    }

    var usersFlow: LiveData<ReceivingUsersResult> = liveData {
        repository._flow.collect {
            emit(it)
        }
    }


}