package com.example.testmed

import android.app.Application
import com.example.testmed.registeruser.di.domainModule
import com.example.testmed.registeruser.di.remoteModule
import com.example.testmed.registeruser.di.repositoryModule
import com.example.testmed.registeruser.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.INFO)
            androidContext(androidContext = applicationContext)
            modules(
                remoteModule,
                repositoryModule,
                domainModule,
                viewModelModule
            )
        }
    }
}