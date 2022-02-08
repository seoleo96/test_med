package com.example.testmed.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ValidatePhone : IValidatePhone {
    override fun validate(phoneNumber: String): Flow<UIValidationState> {
        return when {
            phoneNumber.isEmpty() -> {
                flow {
                    emit(UIValidationState.IsEmpty("Please enter phone number"))
                }
            }
            phoneNumber.length < 17 -> {
                flow {
                    emit(UIValidationState.PhoneNumberLess("Phone number must contain at least 12 digits"))
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