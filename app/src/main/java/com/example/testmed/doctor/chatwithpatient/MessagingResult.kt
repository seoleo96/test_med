package com.example.testmed.doctor.chatwithpatient

import com.example.testmed.model.MessageData

sealed class MessagingResult {
    object Loading : MessagingResult()
    object Empty : MessagingResult()

    data class Error(val errorMessage: String) : MessagingResult()
    data class Success(val data: List<MessageData>) : MessagingResult()
}
