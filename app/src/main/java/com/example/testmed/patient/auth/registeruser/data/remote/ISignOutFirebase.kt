package com.example.testmed.patient.auth.registeruser.data.remote

import com.example.testmed.MainActivity
import kotlinx.coroutines.flow.Flow


interface ISignOutFirebase {

    suspend fun authUser(phoneNumber: String, context: MainActivity)
    fun onCodeSent(): Flow<String>
}