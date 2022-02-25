package com.example.testmed.model

data class PatientData(
    val id: String,
    val iin: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val address: String,
    val birthday: String,
    val gender: String,
    val login: String,
    val password: String,
    val phoneNumber: String,
    val photoUrl: String ="",
    val state: Any,
    val stateTo: String = "1",
    val token: String = "",
) {
    constructor() : this("", "", "", "",
        "", "", "",
        "", "", "", "",
        "", "", "", "")
}