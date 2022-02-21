package com.example.testmed.patient.auth.registeruser.domain.usecase


sealed class UIValidationState {
    object Loading : UIValidationState()
    data class IsEmpty(val errorMessage: String): UIValidationState()
    data class PhoneNumberLess(val errorMessage: String): UIValidationState()
    data class Success<T>(val content: T): UIValidationState()
}