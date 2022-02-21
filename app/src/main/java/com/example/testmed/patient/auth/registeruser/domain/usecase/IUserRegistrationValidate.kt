package com.example.testmed.patient.auth.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IUserRegistrationValidate {
    fun validate(
        etIin: String, etBirthday: String, etCity: String,
        etFio: String, etPhoneNumber: String, etLogin: String,
        etPassword: String, etPatronymic: String, etSurname: String,
    ): Flow<UserRegisterState>
}