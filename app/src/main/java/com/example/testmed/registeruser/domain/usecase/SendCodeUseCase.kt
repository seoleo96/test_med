package com.example.testmed.registeruser.domain.usecase

import com.example.testmed.registeruser.domain.UIState
import com.example.testmed.registeruser.domain.repository.ISendCodeRepository
import kotlinx.coroutines.flow.Flow

class SendCodeUseCase(
    private val sendCodeRepository : ISendCodeRepository
) : ISendCodeUseCase {
    override fun sendCode(code: String, id: String) : Flow<UIState> {
        return sendCodeRepository.sendCode(code, id)
    }
}