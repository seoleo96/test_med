package com.example.testmed.registeruser.domain.repository

import com.example.testmed.MainActivity
import kotlinx.coroutines.flow.Flow

interface ISignOutRepository {
    suspend fun authUser(number: String, context: MainActivity)
    fun onCodeSent(): Flow<String>
}