package com.example.testmed.patient.profile.changepatientdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.patient.auth.registeruser.domain.usecase.IUserRegistrationValidate
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegisterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChangePatientDataViewModel(
    private val userRegistrationValidate: IUserRegistrationValidate,

    ) : ViewModel() {
    private val _dataValidateLiveData: MutableLiveData<UserRegisterState> = MutableLiveData<UserRegisterState>()
    val dataValidateLiveData : LiveData<UserRegisterState> = _dataValidateLiveData

    fun validate(
        etIin: String, etBirthday: String, etCity: String,
        etFio: String, etPhoneNumber: String, etLogin: String,
        etPassword: String, etPatronymic: String, etSurname: String,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            userRegistrationValidate.validate(etIin, etBirthday, etCity,
                etFio, etPhoneNumber, etLogin,
                etPassword, etPatronymic, etSurname).collect {
                _dataValidateLiveData.value = it
            }
        }
    }
}