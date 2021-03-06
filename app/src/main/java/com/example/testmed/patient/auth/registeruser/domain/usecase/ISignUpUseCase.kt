package com.example.testmed.patient.auth.registeruser.domain.usecase

import com.example.testmed.MainActivity
import kotlinx.coroutines.flow.Flow

interface ISignUpUseCase {
    suspend fun authUser(number: String, context: MainActivity)
    fun onCodeSent(): Flow<String>
}