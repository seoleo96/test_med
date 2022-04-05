package com.example.testmed.model

data class MessageData(
    val idMessage: String = "",
    val idFrom: String = "",
    val idTo: String = "",
    val message: String = "",
    val timestamp: Any = "",
    var type: String = "",
    val seen: String = "0",
    val idNotification: Int = 0,
) {
    constructor() : this("", "", "", "", "", "", "", 0)
}