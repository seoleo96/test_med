package com.example.testmed.registeruser.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.MainActivity
import com.example.testmed.registeruser.domain.usecase.ISignUpUseCase
import com.example.testmed.registeruser.domain.usecase.UIValidationState
import com.example.testmed.registeruser.domain.usecase.ValidatePhone
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val useCaseValidatePhone: ValidatePhone,
    private val signUpUseCase: ISignUpUseCase,
) : ViewModel() {

    private val _onCodeSent = MutableLiveData<String>().apply {
        value = ""
    }
    private val _uiValidateLiveData = MutableLiveData<UIValidationState>().apply {
        value = UIValidationState.IsEmpty("")
    }
    val uiValidateLiveData: LiveData<UIValidationState> = _uiValidateLiveData

    val onCodeSent: LiveData<String> = _onCodeSent



    fun validate(phoneNumber: String) {
        viewModelScope.launch {
            useCaseValidatePhone.validate(phoneNumber = phoneNumber).collect {
                _uiValidateLiveData.value = it
            }
        }
    }

    fun onCodeSent() {
        viewModelScope.launch {
            signUpUseCase.onCodeSent().collect { value ->
                _onCodeSent.value = value
            }
        }
    }

    fun authUser(phoneNumber: String, context: MainActivity) {
        viewModelScope.launch {
            signUpUseCase.authUser(number = phoneNumber, context = context)
        }
    }
}