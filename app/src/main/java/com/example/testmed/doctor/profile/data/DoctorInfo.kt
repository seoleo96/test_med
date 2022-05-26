package com.example.testmed.doctor.profile.data

import com.example.testmed.doctor.profile.model.DoctorInfoData
import com.example.testmed.model.DoctorData

interface DoctorInfo {

    fun <T> map(mapper: DoctorProfileMapper<T>): T

    class Base(private val doctorData: DoctorData) : DoctorInfo {
        override fun <T> map(mapper: DoctorProfileMapper<T>): T {
            return mapper.map(doctorData)
        }
    }

    interface DoctorProfileMapper<T> {
        fun map(doctorData: DoctorData): T

        class Base() : DoctorProfileMapper<DoctorInfoData> {
            override fun map(doctorData: DoctorData) = DoctorInfoData(
                fio = "${doctorData.surname} ${doctorData.name} ${doctorData.patronymic}",
                iin = doctorData.iin,
                birthday = doctorData.birthday,
                gender = doctorData.gender,
                login = doctorData.login,
                password = doctorData.password,
                phoneNumber = doctorData.phoneNumber,
                photoUrl = doctorData.photoUrl,
                address = doctorData.address,
                id = doctorData.id,
            )
        }
    }
}