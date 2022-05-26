package com.example.testmed.doctor.profile.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.testmed.doctor.profile.model.DoctorInfoData

class DoctorInfoCommunication : IDoctorInfoCommunication {
    private val liveData = MutableLiveData<DoctorInfoData>()
    override fun map(doctorInfoData: DoctorInfoData) {
        liveData.value = doctorInfoData
    }

    override fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<DoctorInfoData>) {
        liveData.observe(lifecycleOwner, observer)
    }
}