package com.example.testmed.patient.auth.registeruser.domain.usecase

import com.example.testmed.MainActivity
import com.example.testmed.patient.auth.registeruser.domain.repository.ISignOutRepository
import kotlinx.coroutines.flow.Flow

class SignUpUseCase(
    private val signOutRepository: ISignOutRepository,
) : ISignUpUseCase {
    override suspend fun authUser(number: String, context: MainActivity) {
        signOutRepository.authUser(number, context)
    }

    override fun onCodeSent(): Flow<String> {
        return signOutRepository.onCodeSent()
    }
}