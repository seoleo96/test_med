package com.example.testmed.patient.auth.registeruser.di

import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.EnterCodeViewModel
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RegisterViewModel(
            useCaseValidatePhone = get(),
            signUpUseCase = get()
        )
    }

    viewModel {
        EnterCodeViewModel(
            validateCode = get(),
            sendCodeUseCase = get()
        )
    }
}