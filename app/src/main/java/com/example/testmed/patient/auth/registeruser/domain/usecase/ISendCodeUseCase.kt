package com.example.testmed.patient.auth.registeruser.domain.usecase

import com.example.testmed.patient.auth.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCodeUseCase {
    fun sendCode(code : String, id : String) : Flow<UIState>
}