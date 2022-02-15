package com.example.testmed

import android.app.Application
import com.example.testmed.registeruser.data.remote.SendCode
import com.example.testmed.registeruser.data.remote.SignOutFirebase
import com.example.testmed.registeruser.data.repository.SendCodeRepository
import com.example.testmed.registeruser.data.repository.SignOutRepository
import com.example.testmed.registeruser.di.domainModule
import com.example.testmed.registeruser.di.remoteModule
import com.example.testmed.registeruser.di.repositoryModule
import com.example.testmed.registeruser.di.viewModelModule
import com.example.testmed.registeruser.domain.usecase.SendCodeUseCase
import com.example.testmed.registeruser.domain.usecase.SignUpUseCase
import com.example.testmed.registeruser.domain.usecase.ValidateCode
import com.example.testmed.registeruser.domain.usecase.ValidatePhone
import com.example.testmed.registeruser.presentation.viewmodel.EnterCodeViewModel
import com.example.testmed.registeruser.presentation.viewmodel.RegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class TestMedApp : Application() {

    lateinit var registerViewModel : RegisterViewModel
    lateinit var enterCodeViewModel : EnterCodeViewModel


    override fun onCreate() {
        super.onCreate()

        val signOutFirebase = SignOutFirebase()
        val signOutRepository = SignOutRepository(signOutFirebase)
        val useCaseValidatePhone = ValidatePhone()
        val signUpUseCase = SignUpUseCase(signOutRepository)
        registerViewModel = RegisterViewModel(useCaseValidatePhone,signUpUseCase )

        val sendCode = SendCode()
        val sendCodeRepository = SendCodeRepository(sendCode)
        val validateCode = ValidateCode()
        val seCodeUseCase = SendCodeUseCase(sendCodeRepository)
        enterCodeViewModel = EnterCodeViewModel(validateCode, seCodeUseCase)

//        startKoin {
//            androidLogger(Level.INFO)
//            androidContext(androidContext = applicationContext)
//            modules(
//                remoteModule,
//                repositoryModule,
//                domainModule,
//                viewModelModule
//            )
//        }
    }
}