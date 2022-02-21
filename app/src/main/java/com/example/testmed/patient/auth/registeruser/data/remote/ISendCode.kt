package com.example.testmed.patient.auth.registeruser.data.remote

import com.example.testmed.patient.auth.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCode {
    fun sendCode(code: String, id: String): Flow<UIState>
}