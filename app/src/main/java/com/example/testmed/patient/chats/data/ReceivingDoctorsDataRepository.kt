package com.example.testmed.patient.chats.data

import android.util.Log
import com.example.testmed.doctor.home.chats.ReceivingUsersResult
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.MessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.*

class ReceivingDoctorsDataRepository : IReceivingDoctorsRepository {

    private val DB = FirebaseDatabase.getInstance()
    private val UID = FirebaseAuth.getInstance().currentUser?.uid
    private val refMainList = DB.reference.child("main_list").child(UID ?: "")
    private val refDoctors = DB.reference.child("doctors")
    private val refMessages = DB.reference.child("message").child(UID ?: "")
    private var listDoctors = listOf<String>()
    private var listSize = 0
    private var readyList = mutableListOf<CommonPatientData>()
    val _flow = MutableStateFlow<ReceivingUsersResult>(ReceivingUsersResult.Loading)

    override suspend fun receivingDoctorsData() {
        refMainList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listDoctors = snapshot.children.map { it.key.toString() }
                    if (listDoctors.isEmpty()){
                        _flow.value = ReceivingUsersResult.Empty
                    }else{
                        listSize = listDoctors.size
                        getPatientsData()
                    }
                } else {
                    _flow.value = ReceivingUsersResult.Empty
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _flow.value = ReceivingUsersResult.Error(error.message)
            }
        })
    }

    private fun getPatientsData() {
        listDoctors.forEach { idPatient ->
            Log.d("listPatients", idPatient)
            refDoctors.child(idPatient)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var patientData: CommonPatientData? =
                                snapshot.getValue(CommonPatientData::class.java)
                            getPatients(patientData, idPatient)

                        } else {
                            _flow.value = ReceivingUsersResult.Empty
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        _flow.value =
                            ReceivingUsersResult.Error(error.message)
                    }
                })
        }
    }

    private fun getPatients(patientData: CommonPatientData?, idPatient: String) {
        refMessages
            .child(idPatient)
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val messageData: List<MessageData?> =
                            snapshot.children.map {
                                it.getValue(MessageData::class.java)
                            }
                        if (patientData != null && messageData.isNotEmpty()) {
                            patientData.message =
                                messageData[0]?.message ?: ""
                            patientData.idMessage =
                                messageData[0]?.idMessage ?: ""
                            readyList.removeIf {
                                it.id == patientData.id
                            }
                            readyList.add(patientData)
                        }
                        if (listSize == readyList.size) {
                            _flow.value = ReceivingUsersResult.Loading
                            _flow.value = ReceivingUsersResult.Success(readyList)
                        }
                    }else {
                        _flow.value = ReceivingUsersResult.Empty
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _flow.value =
                        ReceivingUsersResult.Error(error.message)
                }
            })
    }
}