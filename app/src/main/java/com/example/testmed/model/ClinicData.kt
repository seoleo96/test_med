package com.example.testmed.model

data class ClinicData(
    val name: String ="",
    val address: String = "",
    val email: String = "",
    val link: String = "",
    val password: String = "",
    val phoneNumber: String = "",
) {
    constructor() : this("", "", "",
        "", "", "")
}