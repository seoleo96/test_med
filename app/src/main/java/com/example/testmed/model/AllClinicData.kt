package com.example.testmed.model

data class AllClinicData(
    val id: String ="",
    val name: String ="",
    val address: String = "",
    val email: String = "",
    val link: String = "",
    val phoneNumber: String = "",
    val specialities: String = "",
    val bank: String = "",
    val bik: String = "",
    val bin: String = "",
    val iik: String = "",
    val rnn: String = "",
    val startEndTime: String = "",
    val imageUrl: String = "",
) {
    constructor() : this("", "", "", "", "",
        "", "", "","", "", "","", "",  "")
}