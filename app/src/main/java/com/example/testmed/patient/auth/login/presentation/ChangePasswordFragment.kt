package com.example.testmed.patient.auth.login.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChangePasswordBinding
import com.example.testmed.model.PatientData
import com.example.testmed.patient.auth.registeruser.domain.usecase.UIValidationState
import com.example.testmed.patient.auth.registeruser.presentation.PhoneTextFormatter
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.RegisterViewModel
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding>(FragmentChangePasswordBinding::inflate) {
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
//                Log.d("CURRENTUSER", it.toString())
                    val data = it.getValue(PatientData::class.java)
                    if (data != null) {
                        list.add(data.phoneNumber)
                    }
                }
            }catch (e : Exception){

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

        registerViewModel.onCodeSent()
        registerViewModel.onCodeSent.observe(viewLifecycleOwner) { code ->
            if (code == "") {
                binding.textLogin.text = "Изменить пароль"
            } else {
                binding.textLogin.text = code
                val action =
                    ChangePasswordFragmentDirections.actionChangePasswordFragmentToNavigationEnter(
                        code, PHONE_NUMBER, "change")
                findNavController().navigate(action)
            }
        }

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
                        if (list.contains(result)) {
                            invisibleData()
                            registerViewModel.authUser(result, requireActivity() as MainActivity)
                        } else {
                            showSnackbar("Номер не зарегистрирован")
                        }
                    }
                }
            }
        }
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