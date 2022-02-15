package com.example.testmed.registeruser.data.remote

import com.example.testmed.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCode {
    fun sendCode(code: String, id: String): Flow<UIState>
}