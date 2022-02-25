package com.example.testmed

import android.app.Application
import android.util.Log
import com.example.testmed.doctor.home.chats.PatientViewModel
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
import com.google.firebase.messaging.FirebaseMessaging


class TestMedApp : Application() {

    lateinit var registerViewModel: RegisterViewModel
    lateinit var enterCodeViewModel: EnterCodeViewModel
    lateinit var patientsDataViewModel: PatientViewModel

    override fun onCreate() {
        super.onCreate()
        Log.e("TOKENONCREATE", "oncreate")

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                Log.d("TOKENCHORT", token!!)
            }

        val signOutFirebase = SignOutFirebase()
        val signOutRepository = SignOutRepository(signOutFirebase)
        val useCaseValidatePhone = ValidatePhone()
        val signUpUseCase = SignUpUseCase(signOutRepository)
        registerViewModel = RegisterViewModel(useCaseValidatePhone, signUpUseCase)

        val sendCode = SendCode()
        val sendCodeRepository = SendCodeRepository(sendCode)
        val validateCode = ValidateCode()
        val seCodeUseCase = SendCodeUseCase(sendCodeRepository)
        enterCodeViewModel = EnterCodeViewModel(validateCode, seCodeUseCase)


    }
}