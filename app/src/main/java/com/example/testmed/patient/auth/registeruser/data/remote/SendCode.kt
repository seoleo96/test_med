package com.example.testmed.patient.auth.registeruser.data.remote

import com.example.testmed.AUTH
import com.example.testmed.patient.auth.registeruser.domain.UIState
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SendCode : ISendCode {
    private val checkCodeFlow: MutableStateFlow<UIState> = MutableStateFlow(UIState.Loading)
    override fun sendCode(code: String, id: String): Flow<UIState> {
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful){
                checkCodeFlow.value = UIState.Success("true")
            }else{
                checkCodeFlow.value = UIState.Error("Код введён неверно")
            }
        }
        return checkCodeFlow
    }
}