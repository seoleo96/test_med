package com.example.testmed.patient.auth.registeruser.di

import com.example.testmed.patient.auth.registeruser.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    factory<ISignUpUseCase> {
        SignUpUseCase(signOutRepository = get())
    }

    factory<ValidatePhone> {
        ValidatePhone()
    }

    factory<ISendCodeUseCase> {
        SendCodeUseCase(sendCodeRepository = get())
    }

    factory<IValidateCode> {
        ValidateCode()
    }
}