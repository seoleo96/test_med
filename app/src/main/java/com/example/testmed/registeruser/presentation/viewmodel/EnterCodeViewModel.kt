package com.example.testmed.registeruser.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.registeruser.domain.UIState
import com.example.testmed.registeruser.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EnterCodeViewModel(
    private val validateCode: IValidateCode,
    private val sendCodeUseCase: ISendCodeUseCase,
) : ViewModel() {
    private val _uiCodeValidateLiveData = MutableLiveData<UIValidationState>().apply {
        value = UIValidationState.Loading
    }
    val uiCodeValidateLiveData: LiveData<UIValidationState> = _uiCodeValidateLiveData
    private val _sendCodeLiveData = MutableLiveData<UIState>().apply {
        value = UIState.Loading
    }
    val sendCodeLiveData: LiveData<UIState> = _sendCodeLiveData

    fun validate(code: String) {
        viewModelScope.launch {
            validateCode.validate(code).collect {
                _uiCodeValidateLiveData.value = it
            }
        }
    }

    fun enterCode(code: String, id: String) {
        viewModelScope.launch {
            _sendCodeLiveData.value = UIState.Loading
            sendCodeUseCase.sendCode(code, id).collect {
                _sendCodeLiveData.value = it
            }
        }
    }
}