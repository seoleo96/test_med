package com.example.testmed.patient.speciality.consulting.presentation

class ScheduleTimes {
    operator fun times(time: Int): String {
        return when (time) {
            0 -> "09-00"
            1 -> "10-00"
            2 -> "11-00"
            4 -> "12-00"
            5 -> "14-00"
            6 -> "15-00"
            7 -> "16-00"
            8 -> "17-00"
            else -> "Closed"
        }
    }
}