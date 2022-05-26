package com.example.testmed.model

data class RecommendationData(
    val idDoctor: String,
    val patientId: String,
    val doctorFio: String,
    val doctorSpeciality: String,
    val patientFio: String,
    val timestamp: Any,
    val recommendationUrl: String,
    val idRecommendation: String,
) {
    constructor() : this("", "", "", "", "", "", "", "")
}
