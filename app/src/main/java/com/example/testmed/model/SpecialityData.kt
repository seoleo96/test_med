package com.example.testmed.model

data class SpecialityData(
    val id : String = "",
    val idClinic : String = "",
    val speciality: String = "",
    val imageUrl: String = "",
    val purpose: String = ""
) {
    constructor() : this("", "", "", "", "")
}