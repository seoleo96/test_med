package com.example.testmed.registeruser.domain.usecase

import com.example.testmed.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCodeUseCase {
    fun sendCode(code : String, id : String) : Flow<UIState>
}