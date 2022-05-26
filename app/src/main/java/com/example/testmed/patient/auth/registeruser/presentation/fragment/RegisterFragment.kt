package com.example.testmed.patient.auth.registeruser.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragmentAuth
import com.example.testmed.databinding.FragmentRegisterBinding
import com.example.testmed.model.PatientData
import com.example.testmed.patient.auth.registeruser.domain.usecase.UIValidationState
import com.example.testmed.patient.auth.registeruser.presentation.PhoneTextFormatter
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.RegisterViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


class RegisterFragment :
    BaseFragmentAuth<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private lateinit var phoneNumber: String

    //    private val registerViewModel: RegisterViewModel by viewModel()
    private lateinit var registerViewModel: RegisterViewModel
    private val list = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = (activity?.application as TestMedApp).registerViewModel

        lifecycleScope.launch {
            try {
                val temp: DataSnapshot = DB.reference.child("patients")
                    .get().await()
                temp.children.forEach {
                    Log.d("CURRENTUSER", it.toString())
                    val data = it.getValue(PatientData::class.java)
                    if (data != null) {
                        list.add(data.phoneNumber)
                    }

                }
            } catch (e: Exception) {

            }

        }

        binding.apply {
            editTextPhone.addTextChangedListener(PhoneTextFormatter(editTextPhone,
                "+# (###) ###-####"))

            editTextPhone.doAfterTextChanged {
                if (it?.length == 17) {
                    view.hideKeyboard()
                }
            }
        }

//        registerViewModel.onCodeSent()
//        registerViewModel.onCodeSent.observe(viewLifecycleOwner) { code ->
//            if (code == "") {
//                binding.textLogin.text = "Регистрация"
//            } else {
//                binding.textLogin.text = code
//                val action =
//                    RegisterFragmentDirections.actionNavigationRegisterToEnterCodeFragment(
//                        code, PHONE_NUMBER, "register")
//                registerViewModel.setNull()
//                findNavController().navigate(action)
//            }
//        }

        binding.sendUsersDataButton.setOnClickListener {
            phoneNumber = binding.editTextPhone.text.toString()
            it.hideKeyboard()
            registerViewModel.validate(phoneNumber = phoneNumber)
            registerViewModel.uiValidateLiveData.observe(viewLifecycleOwner) { uiState ->
                when (uiState) {
                    is UIValidationState.IsEmpty -> {
                        binding.editTextPhone.apply {
                            error = uiState.errorMessage
                            requestFocus()
                        }
                    }
                    is UIValidationState.PhoneNumberLess -> {
                        binding.editTextPhone.apply {
                            error = uiState.errorMessage
                            requestFocus()
                        }
                    }
                    else -> {
                        val regex = Regex("[^+0-9]")
                        val result = regex.replace(phoneNumber, "")
                        PHONE_NUMBER = result
                        if (!list.contains(result)) {
                            invisibleData()
                            authUser(result)
                        } else {
                            showSnackbar("Номер уже зарегистрирован")
                        }
                    }
                }
            }
        }
    }

    private var mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("credential", credential.smsCode.toString())
        }

        override fun onVerificationFailed(p0: FirebaseException) {}
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            if (verificationId == "") {
                binding.textLogin.text = "Регистрация"
            } else {
                binding.textLogin.text = verificationId
                val action =
                    RegisterFragmentDirections.actionNavigationRegisterToEnterCodeFragment(
                        verificationId, PHONE_NUMBER, "register")
                registerViewModel.setNull()
                findNavController().navigate(action)
            }
        }
    }

    private fun authUser(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(AUTH())
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())             // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun invisibleData() {
        binding.apply {
            vector.isVisible = false
            progressBar.isVisible = true
            editTextPhone.isVisible = false
            textLogin.isVisible = false
            sendUsersDataButton.isVisible = false
            textPhoneNumber.isVisible = false
//            tvToRegisterEmail.isVisible = false
        }
    }
}