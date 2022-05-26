package com.example.testmed.doctor.profile.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.testmed.doctor.profile.model.DoctorInfoData

interface IDoctorInfoCommunication {
    fun map(doctorInfoData: DoctorInfoData)
    fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<DoctorInfoData>)
}