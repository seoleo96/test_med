package com.example.testmed.doctor.chatwithpatient.data

import android.util.Log
import com.example.testmed.UID
import com.example.testmed.doctor.chatwithpatient.MessagingResult
import com.example.testmed.model.MessageData
import com.example.testmed.model.PatientData
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PatientsDataRepository : IPatientsDataRepository {

    private val DB = FirebaseDatabase.getInstance()
    private val reference = DB.reference.child("patients")

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
}