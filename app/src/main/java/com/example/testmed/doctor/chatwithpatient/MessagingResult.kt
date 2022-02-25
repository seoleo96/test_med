package com.example.testmed.doctor.chatwithpatient

import com.example.testmed.model.MessageData
import com.google.firebase.database.DataSnapshot

sealed class MessagingResult {
    object Loading : MessagingResult()
    object Empty : MessagingResult()

    data class Error(val errorMessage: String) : MessagingResult()
    data class Success(val data: DataSnapshot) : MessagingResult()
    data class SuccessList(val data: List<MessageData>) : MessagingResult()
}
