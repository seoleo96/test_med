package com.example.testmed.registeruser.data.repository

import android.content.Context
import com.example.testmed.MainActivity
import com.example.testmed.registeruser.data.remote.SignOutFirebase
import com.example.testmed.registeruser.domain.repository.ISignOutRepository
import kotlinx.coroutines.flow.Flow

class SignOutRepository(private val signOutFirebase: SignOutFirebase) : ISignOutRepository {
    override suspend fun authUser(number: String, context: MainActivity) {
        signOutFirebase.authUser(number, context)
    }

    override fun onCodeSent(): Flow<String> {
        return signOutFirebase.onCodeSent()
    }
}