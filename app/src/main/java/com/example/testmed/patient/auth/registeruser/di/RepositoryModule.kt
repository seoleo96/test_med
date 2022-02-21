package com.example.testmed.patient.auth.registeruser.di


import com.example.testmed.patient.auth.registeruser.data.repository.SendCodeRepository
import com.example.testmed.patient.auth.registeruser.data.repository.SignOutRepository
import com.example.testmed.patient.auth.registeruser.domain.repository.ISendCodeRepository
import com.example.testmed.patient.auth.registeruser.domain.repository.ISignOutRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<ISignOutRepository> {
        SignOutRepository(signOutFirebase = get())
    }

    factory<ISendCodeRepository> {
        SendCodeRepository(sendCodeRemote = get())
    }
}