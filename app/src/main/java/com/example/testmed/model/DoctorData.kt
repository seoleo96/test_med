package com.example.testmed.model

data class DoctorData(
    val id: String,
    val idSpeciality: String,
    val iin: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val speciality: String,
    val specialization: String,
    val experience: String,
    val costOfConsultation: String,
    val education: String,
    val address: String,
    val birthday: String,
    val gender: String,
    val login: String,
    val password: String,
    val phoneNumber: String,
    val state: Any,
    val stateTo: String = "",
    var photoUrl: String ="",
    val seen : String = "0",
    val token :  String,
    val idClinic :  String = "",
) {
    constructor() : this("","","", "","", "",
        "", "", "","","",
        "", "", "",
        "", "", "", "",
        "", "", "", "")
}