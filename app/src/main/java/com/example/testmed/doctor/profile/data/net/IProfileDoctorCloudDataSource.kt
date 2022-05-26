package com.example.testmed.doctor.profile.data.net

import com.example.testmed.doctor.profile.data.DoctorInfo


interface IProfileDoctorCloudDataSource {
    suspend fun fetchDoctorData(idDoctor : String) : DoctorInfo
}