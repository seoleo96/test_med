package com.example.testmed.registeruser.di


import com.example.testmed.registeruser.data.repository.SendCodeRepository
import com.example.testmed.registeruser.data.repository.SignOutRepository
import com.example.testmed.registeruser.domain.repository.ISendCodeRepository
import com.example.testmed.registeruser.domain.repository.ISignOutRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<ISignOutRepository> {
        SignOutRepository(signOutFirebase = get())
    }

    factory<ISendCodeRepository> {
        SendCodeRepository(sendCodeRemote = get())
    }
}