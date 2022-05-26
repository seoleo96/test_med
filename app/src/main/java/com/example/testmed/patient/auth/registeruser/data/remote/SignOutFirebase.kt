package com.example.testmed.patient.auth.registeruser.data.remote

import android.util.Log
import com.example.testmed.AUTH
import com.example.testmed.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class SignOutFirebase : ISignOutFirebase {
    private var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val _onCodeSentFlow: MutableStateFlow<String> = MutableStateFlow("")

    init {
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("credential", credential.smsCode.toString())
            }
            override fun onVerificationFailed(p0: FirebaseException) {}
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                if (verificationId.isEmpty()) {
                    _onCodeSentFlow.value = ""
                } else {
                    Log.d("credentialverificationId", verificationId)
                    _onCodeSentFlow.value = verificationId
                }
            }
        }
    }

    override suspend fun authUser(phoneNumber: String, context: MainActivity) {
        val options = PhoneAuthOptions.newBuilder(AUTH())
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context)             // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onCodeSent(): Flow<String> {
        return _onCodeSentFlow
    }
}