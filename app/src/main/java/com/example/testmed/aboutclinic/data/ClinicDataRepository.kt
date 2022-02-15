package com.example.testmed.aboutclinic.data

import com.example.testmed.DB
import com.example.testmed.aboutclinic.ClinicDataResult
import com.example.testmed.model.ClinicData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ClinicDataRepository : IClinicDataRepository {
    private val mutableStateFlow = MutableStateFlow<ClinicDataResult>(ClinicDataResult.Loading)
    override fun fetchClinicData(): Flow<ClinicDataResult> {
        DB.reference
            .child("clinic")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(ClinicData::class.java)
                    if (data != null) {
                        mutableStateFlow.value = ClinicDataResult.Success(data)
                    } else {
                        mutableStateFlow.value = ClinicDataResult.Error("Проверьте интернет подключение.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        return mutableStateFlow
    }
}