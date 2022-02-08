package com.example.testmed.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IValidateCode {
    fun validate(code: String): Flow<UIValidationState>
}