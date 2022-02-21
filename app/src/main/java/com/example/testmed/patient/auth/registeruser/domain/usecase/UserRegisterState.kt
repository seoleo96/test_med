package com.example.testmed.patient.auth.registeruser.domain.usecase

sealed class UserRegisterState{
    object Loading : UserRegisterState()
    data class AllDataEmpty(val errorMessage: String): UserRegisterState()
    object IinEmpty: UserRegisterState()
    data class IinLengthLess(val errorMessage: String): UserRegisterState()
    object FIOEmpty : UserRegisterState()
    object SurnameEmpty : UserRegisterState()
    object PatronymicEmpty : UserRegisterState()
    object CitEmpty : UserRegisterState()
    object BirthdayEmpty : UserRegisterState()
    object PhoneNumberEmpty : UserRegisterState()
    data class PhoneNumberLengthLess(val errorMessage: String) : UserRegisterState()
    object LoginEmpty : UserRegisterState()
    data class LoginValidate(val errorMessage: String) : UserRegisterState()
    object PasswordEmpty : UserRegisterState()
    data class PasswordLengthLess(val errorMessage: String) : UserRegisterState()
    object Success : UserRegisterState()
}
