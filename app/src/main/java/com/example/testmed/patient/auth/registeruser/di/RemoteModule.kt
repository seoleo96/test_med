package com.example.testmed.patient.auth.registeruser.di


import com.example.testmed.patient.auth.registeruser.data.remote.ISendCode
import com.example.testmed.patient.auth.registeruser.data.remote.SendCode
import com.example.testmed.patient.auth.registeruser.data.remote.SignOutFirebase
import org.koin.dsl.module


val remoteModule = module {
    factory<SignOutFirebase> {
        SignOutFirebase()
    }

    factory<ISendCode> {
        SendCode()
    }
}
