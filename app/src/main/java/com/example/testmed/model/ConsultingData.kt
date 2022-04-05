package com.example.testmed.model

data class ConsultingData(
    val idConsulting: String = "",
    val idFrom: String = "",
    val idTo: String = "",
    val link: String = "",
    val status : String =""

) {
    constructor() : this("", "", "","" )
}