package com.example.testmed.patient.auth.registeruser.data.repository

import com.example.testmed.patient.auth.registeruser.data.remote.ISendCode
import com.example.testmed.patient.auth.registeruser.domain.UIState
import com.example.testmed.patient.auth.registeruser.domain.repository.ISendCodeRepository
import kotlinx.coroutines.flow.Flow

class SendCodeRepository(
    private val sendCodeRemote: ISendCode,
) : ISendCodeRepository {
    override fun sendCode(code: String, id: String): Flow<UIState> {
        return sendCodeRemote.sendCode(code, id)
    }
}