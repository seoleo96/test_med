package com.example.testmed.patient.auth.registeruser.domain.repository

import com.example.testmed.patient.auth.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCodeRepository {
    fun sendCode(code : String, id : String) : Flow<UIState>
}