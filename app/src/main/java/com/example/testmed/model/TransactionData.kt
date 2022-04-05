package com.example.testmed.model

data class TransactionData(
    val cardNumber: String ="",
    val costOfConsultation: String = "",
    val date: String = "",
    val idDoctor: String = "",
    val idPatient: String = "",
    val time: String = "",
    val idTransaction: String = "",
    val recordingDate: String = "",
    val statusConsulting: String = "",
    val bank: String = "",
    val bin: String = "",
    val bik: String = "",
    val iik: String = "",
    val idClinic: String = "",
    val confirmation: String = "",
) {
    constructor() : this("", "", "","","",
        "", "")
}