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
    var sizeNotReadingMessages: String = "",

    var idMessage: String = "",
    val idPatient: String = "",
    val idDoctor: String = "",
    var message: String = "",
    var timestamp: Any = "",
    val type: String = "",
    val seen: String = "",
    val idFrom: String = "",
    val idTo: String = "",

    val fullName : String = "",
    val time : String = "",
    val idNotification: String = "",


    val cardNumber: String ="",
    val costOfConsultation: String = "",
    val date: String = "",
    val idTransaction: String = "",
    val fullNamePatient: String = "",
    val fullNameDoctor: String = "",
    val speciality: String = "",
    val recordingDate: String = "",
    val statusConsulting : String = "",
    val photoUrlPatient : String = "",

    val confirmation : String = "",
)
