package com.example.testmed.model

data class ClinicData(
    val name: String ="",
    val address: String = "",
    val email: String = "",
    val link: String = "",
    val id: String = "",
    val phoneNumber: String = "",
) {
    constructor() : this("", "", "",
        "", "", "")
}