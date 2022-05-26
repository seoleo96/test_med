package com.example.testmed.doctor.profile.data.net

import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.doctor.profile.data.DoctorInfo
import com.example.testmed.model.DoctorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileDoctorCloudDataSource : IProfileDoctorCloudDataSource {

    override suspend fun fetchDoctorData(idDoctor: String): DoctorInfo {
        val docRef = DB.reference.child("doctors").child(UID())
        val doctorData: DoctorData = handleResult(docRef)
        return DoctorInfo.Base(doctorData)
    }

    private suspend fun handleResult(reference: DatabaseReference): DoctorData =
        suspendCoroutine { cont ->
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        cont.resume(snapshot.getValue(DoctorData::class.java)!!)
                    }catch (e:Exception){}
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
        }
}