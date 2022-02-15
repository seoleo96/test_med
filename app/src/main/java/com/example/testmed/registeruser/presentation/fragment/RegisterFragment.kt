package com.example.testmed.registeruser.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentRegisterBinding
import com.example.testmed.registeruser.domain.usecase.UIValidationState
import com.example.testmed.registeruser.presentation.PhoneTextFormatter
import com.example.testmed.registeruser.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private lateinit var phoneNumber: String
//    private val registerViewModel: RegisterViewModel by viewModel()
    private lateinit var registerViewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = (activity?.application as TestMedApp).registerViewModel

        binding.tvToRegisterEmail.setOnClickListener {
            findNavController().navigate(R.id.navigation_userDataSignOutFragment)
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
                binding.textLogin.text = "Регистрация"
            } else {
                binding.textLogin.text = code
                val action =
                    RegisterFragmentDirections.actionNavigationRegisterToEnterCodeFragment(
                        code, PHONE_NUMBER)
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
                        invisibleData()
                        val regex = Regex("[^+0-9]")
                        val result = regex.replace(phoneNumber, "")
                        PHONE_NUMBER = result
                        registerViewModel.authUser(result, requireActivity() as MainActivity)
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
        }
    }
}