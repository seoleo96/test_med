package com.example.testmed.patient.auth.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IValidatePhone {
    fun validate(phoneNumber: String): Flow<UIValidationState>
}