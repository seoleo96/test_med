package com.example.testmed.model

data class CommentsData(
    val idMessage: String = "",
    val idFrom: String = "",
    val idTo: String = "",
    val message: String = "",
    val timestamp: Any = "",
    val username: String = "",
) {
    constructor() : this("", "", "", "", "")
}