package com.example.testmed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Login(
    val login : String?,
    val password : String?
) : Parcelable