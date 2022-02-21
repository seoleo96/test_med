package com.example.testmed.model

data class MessageData(
    val idMessage: String = "",
    val idFrom: String = "",
    val idTo: String = "",
    val message: String = "",
    val timestamp: Any = "",
    val type: String = "",
) {
    constructor() : this("", "", "", "", "", "")
}