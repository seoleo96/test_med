package com.example.testmed.patient.auth.registeruser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.regex.Pattern

class UserRegistrationValidate : IUserRegistrationValidate {
    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun validate(
        etIin: String,
        etBirthday: String,
        etCity: String,
        etFio: String,
        etPhoneNumber: String,
        etLogin: String,
        etPassword: String,
        etPatronymic: String,
        etSurname: String,
        city: String,
    ): Flow<UserRegisterState> {
        val yearIIN = etIin.substring(0, 2).toInt()
        val monthIIN = etIin.substring(2, 4).toInt()
        val dayIIN = etIin.substring(4, 6).toInt()
        val gender: Char = etIin[6]
        val g = "$gender".toInt()
        if (etIin.isEmpty() && etBirthday.isEmpty() && etCity.isEmpty() &&
            etFio.isEmpty() && etLogin.isEmpty() &&
            etPassword.isEmpty() && etPatronymic.isEmpty() && etSurname.isEmpty()
        ) {
            return flow {
                emit(UserRegisterState.AllDataEmpty("Запольните всех полей."))
            }
        } else if (etIin.isEmpty()) {
            return flow {
                emit(UserRegisterState.IinEmpty)
            }
        } else if (etIin.length < 12) {
            return flow {
                emit(UserRegisterState.IinLengthLess("Номер ИИН должен быть 12 цифр, попробуйте еще раз."))
            }
        } else if (yearIIN !in 1..99 || monthIIN !in 1..12 || dayIIN !in 1..31) {
            return flow {
                emit(UserRegisterState.IinLengthLess("Проверьте правильность ИИН."))
            }
        } else if (g !in 3..6) {
            return flow {
                emit(UserRegisterState.IinLengthLess("Проверьте правильность ИИН."))
            }
        } else if (etFio.isEmpty()) {
            return flow {
                emit(UserRegisterState.FIOEmpty)
            }
        } else if (etSurname.isEmpty()) {
            return flow {
                emit(UserRegisterState.SurnameEmpty)
            }
        }else if (etCity.isEmpty()) {
            return flow {
                emit(UserRegisterState.CitEmpty)
            }
        }else if (city.isEmpty()) {
            return flow {
                emit(UserRegisterState.CityEmpty)
            }
        } else if (etBirthday.isEmpty()) {
            return flow {
                emit(UserRegisterState.BirthdayEmpty)
            }
        } else if (etPhoneNumber.isEmpty()) {
            return flow {
                emit(UserRegisterState.PhoneNumberEmpty)
            }
        } else if (etPhoneNumber.length < 12) {
            return flow {
                emit(UserRegisterState.PhoneNumberLengthLess("Номер телефона не соответсвует, попробуйте еще раз"))
            }
        } else if (etLogin.isEmpty()) {
            return flow {
                emit(UserRegisterState.LoginEmpty)
            }
        } else if (!EMAIL_ADDRESS_PATTERN.matcher(etLogin).matches()) {
            return flow {
                emit(UserRegisterState.LoginValidate("Недействительные учетные данные, попробуйте еще раз."))
            }
        } else if (etPassword.isEmpty()) {
            return flow {
                emit(UserRegisterState.PasswordEmpty)
            }
        } else if (etPassword.length < 6) {
            return flow {
                emit(UserRegisterState.PasswordLengthLess("Длина пароля должна быть более шести символов."))
            }
        } else
            return flow {
                emit(UserRegisterState.Success)
            }
    }
}