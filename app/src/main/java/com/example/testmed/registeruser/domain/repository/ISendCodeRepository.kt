package com.example.testmed.registeruser.domain.repository

import com.example.testmed.registeruser.domain.UIState
import kotlinx.coroutines.flow.Flow

interface ISendCodeRepository {
    fun sendCode(code : String, id : String) : Flow<UIState>
}