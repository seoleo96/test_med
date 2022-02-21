package com.example.testmed

import android.app.Application
import com.example.testmed.doctor.chatwithpatient.presentation.ChatWithPatientViewModel
import com.example.testmed.doctor.home.chats.PatientViewModel
import com.example.testmed.doctor.home.chats.data.ReceivingPatientsDataRepository
import com.example.testmed.patient.auth.registeruser.data.remote.SendCode
import com.example.testmed.patient.auth.registeruser.data.remote.SignOutFirebase
import com.example.testmed.patient.auth.registeruser.data.repository.SendCodeRepository
import com.example.testmed.patient.auth.registeruser.data.repository.SignOutRepository
import com.example.testmed.patient.auth.registeruser.domain.usecase.SendCodeUseCase
import com.example.testmed.patient.auth.registeruser.domain.usecase.SignUpUseCase
import com.example.testmed.patient.auth.registeruser.domain.usecase.ValidateCode
import com.example.testmed.patient.auth.registeruser.domain.usecase.ValidatePhone
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.EnterCodeViewModel
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.RegisterViewModel


class TestMedApp : Application() {

    lateinit var registerViewModel : RegisterViewModel
    lateinit var enterCodeViewModel : EnterCodeViewModel
    lateinit var patientsDataViewModel : PatientViewModel

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