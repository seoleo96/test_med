package com.example.testmed.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotifyData(
    val fromId: String,
    val toId: String,
    val body: String,
    val title: String,
    val idNotification: Int,
    val fromWho: String,// 0-patient 1-doctor
    val icon: Int,
    val type: String,
) : Parcelable
