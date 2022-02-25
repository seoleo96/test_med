package com.example.testmed.model

data class CommonPatientData(
    val id: String = "",
    val iin: String = "",
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val address: String = "",
    val birthday: String = "",
    val gender: String = "",
    val login: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val photoUrl: String? = "",

    var idMessage: String = "",
    val idPatient: String = "",
    val idDoctor: String = "",
    var message: String = "",
    val timestamp: Any = "",
    val type: String = "",

    val fullName : String = "",
    val time : String = "",
    val idNotification: String = ""
)
