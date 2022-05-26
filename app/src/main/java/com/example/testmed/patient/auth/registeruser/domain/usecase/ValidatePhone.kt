package com.example.testmed.patient.auth.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ValidatePhone : IValidatePhone {
    override fun validate(phoneNumber: String): Flow<UIValidationState> {
        return when {
            phoneNumber.isEmpty() -> {
                flow {
                    emit(UIValidationState.IsEmpty("Пожалуйста, введите номер телефона"))
                }
            }
            phoneNumber.length < 17 -> {
                flow {
                    emit(UIValidationState.PhoneNumberLess("Номер телефона должен содержать не менее 12 цифр"))
                }
            }
            else -> {
                flow {
                    emit(UIValidationState.Success("validate"))
                }
            }
        }
    }
}