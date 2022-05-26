package com.example.testmed.doctor.profile.model

data class DoctorInfoData(
    val fio: String,
    val iin: String,
    val birthday: String,
    val gender: String,
    val login: String,
    val password: String,
    val phoneNumber: String,
    val photoUrl: String ="",
    val address: String,
    val id: String,
)
