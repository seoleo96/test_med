package com.example.testmed.doctor.chatwithpatient.data

import android.util.Log
import com.example.testmed.UID
import com.example.testmed.doctor.chatwithpatient.MessagingResult
import com.example.testmed.model.MessageData
import com.example.testmed.model.PatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PatientsDataRepository : IPatientsDataRepository {

    private val DB = FirebaseDatabase.getInstance()
    private val reference = DB.reference.child("patients")
    private val refMessages = com.example.testmed.DB.reference.child("message").child(UID())
    private var listMessages = mutableListOf<MessageData>()
    val _flow = MutableStateFlow<MessagingResult>(MessagingResult.Loading)

    override suspend fun getDoctorsData(idPatient: String): PatientDataResult =
        suspendCoroutine<PatientDataResult> { cont ->
            reference
                .child(idPatient)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val patientData = snapshot.getValue(PatientData::class.java)!!
                            cont.resume(PatientDataResult.Success(patientData))
                        } else {
                            cont.resume(PatientDataResult.NotExist)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }

    override suspend fun getMessages(idPatient: String): MessagingResult =
        suspendCancellableCoroutine { cont ->
            refMessages
                .child(idPatient)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { messages ->
                                val data = messages.getValue(MessageData::class.java)
                                if (data != null) {
                                    listMessages.removeIf {
                                        it.idMessage == data.idMessage
                                    }
                                    listMessages.add(data)
                                    cont.resume(MessagingResult.Success(listMessages))
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
}