package com.example.testmed.doctor.home.chats

import com.example.testmed.model.CommonPatientData

sealed class ReceivingUsersResult {
    object Loading : ReceivingUsersResult()
    object Empty : ReceivingUsersResult()

    data class Error(val errorMessage: String) : ReceivingUsersResult()
    data class SuccessList(val data: List<CommonPatientData>) : ReceivingUsersResult()
    data class Success(val data: Map<CommonPatientData, String>) : ReceivingUsersResult()
}
