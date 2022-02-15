package com.example.testmed.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IValidatePhone {
    fun validate(phoneNumber: String): Flow<UIValidationState>
}