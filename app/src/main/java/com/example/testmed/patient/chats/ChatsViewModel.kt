package com.example.testmed.patient.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.testmed.doctor.home.chats.ReceivingUsersResult
import com.example.testmed.patient.chats.data.ReceivingDoctorsDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val repository: ReceivingDoctorsDataRepository,
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.receivingDoctorsData()
        }
    }

    var usersFlow: LiveData<ReceivingUsersResult> = liveData {
        repository._flow.collect {
            emit(it)
        }
    }
}