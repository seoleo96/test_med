package com.example.testmed.patient.auth.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ValidateCode : IValidateCode {
    private val validateStateFlow = MutableStateFlow<UIValidationState>(UIValidationState.Loading)
    override fun validate(code: String): Flow<UIValidationState> {
        when {
            code.isEmpty() -> {
                validateStateFlow.value = UIValidationState.IsEmpty("")
            }
            code.length < 6 -> {
                validateStateFlow.value =
                    UIValidationState.PhoneNumberLess("Код должен содержить 6 цифр")
            }
            else -> {
                validateStateFlow.value =
                    UIValidationState.Success("validate")
            }
        }
        return validateStateFlow
    }
}