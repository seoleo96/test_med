package com.example.testmed.doctor.chatwithpatient.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.testmed.*
import com.example.testmed.doctor.chatwithpatient.data.PatientDataResult
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.example.testmed.model.MessageData
import com.example.testmed.model.PatientData
import com.example.testmed.notification.NotifyData
import com.example.testmed.notification.RetrofitClient
import com.example.testmed.notification.Sender
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ChatWithPatientViewModel() : ViewModel() {

    val livedata: MutableLiveData<PatientData> = MutableLiveData()

    fun getPatientsData(idPatient: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DB.reference
                .child("patients").child(idPatient)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(PatientData::class.java)
                            viewModelScope.launch {
                                livedata.value = data!!
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    fun saveMainList(
        patientId: String,
        photoUrl: String,
        text: String,
        timestamp: Any,
        type: String,
    ) {
        val doctorId = UID()
        viewModelScope.launch(Dispatchers.IO) {
            val refPatient = "main_list/${patientId}/$doctorId"
            val refDoctor = "main_list/$doctorId/${patientId}"
            val childPatient = CommonPatientData(
                id = patientId,
                photoUrl = photoUrl,
                message = text,
                timestamp = timestamp,
                type = type
            )
            val childDoctor = CommonPatientData(
                id = doctorId,
                photoUrl = photoUrl,
                message = text,
                timestamp = ServerValue.TIMESTAMP,
                type = type
            )
            DB.reference.child(refPatient).setValue(childPatient)
            DB.reference.child(refDoctor).setValue(childDoctor)
        }
    }

    fun sendMessage(
        idMess: String,
        patientId: String,
        text: String,
        timestamp: Any,
        type: String,
        idNotification: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val message = MessageData(
                idMessage = idMess,
                idFrom = UID(),
                idTo = patientId,
                message = text,
                timestamp = timestamp,
                type = type,
                seen = "0",
                idNotification = idNotification
            )
            val refDoctor = "message/${UID()}/${patientId}"
            val refPatient = "message/${patientId}/${UID()}"

            DB.reference.child(refPatient).child(idMess).setValue(message)
            DB.reference.child(refDoctor).child(idMess).setValue(message)
        }
    }

    fun sendNotificationData(idPatient: String, message: String, idNotification: Int) {
        val idDoc = UID()
        viewModelScope.launch(Dispatchers.IO) {
            val reDoctorToken = DB.reference.child("doctors").child(idDoc).child("token")
            val refPatientToken = DB.reference.child("patients").child(idPatient).child("token")
            val refDoctorName = DB.reference.child("doctors").child(idDoc).child("name")
            val refDoctorSurname = DB.reference.child("doctors").child(idDoc).child("surname")
            val name = refDoctorName.get().await().getValue(String::class.java)
            val surname = refDoctorSurname.get().await().getValue(String::class.java)
            val titleName = "$name $surname"
            val notifyData = NotifyData(
                fromId = idDoc,
                title = titleName,
                icon = 1,
                body = message,
                idNotification = idNotification,
                fromWho = "1",
                toId = idPatient
            )
            val tokenDoc = reDoctorToken.get().await().getValue(String::class.java)
            val tokenPat = refPatientToken.get().await().getValue(String::class.java)
            Log.d("TOCENDOC", "$tokenDoc")
            Log.d("TOCENPAT", "$tokenPat")
            val sender = Sender(notifyData, tokenPat!!)
            Log.d("SENDER", sender.toString())
            sendNotification(sender)
        }
    }

    private fun sendNotification(notification: Sender) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.sendNotification(notification)
            if (response.isSuccessful) {
                val data: ResponseData = response.body()!!
                Log.d("TAG", "ResponseBodyLog: $data")
                Log.d("TAG", "Response: $data")
                Log.d("TAG", "Response: ${response.body().toString()}")
            } else {
                Log.e("TAG", response.errorBody().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", e.message.toString())
        }
    }

    fun updateStateTo(idDoctor: String, id: String) {
        val refStateTo = DB.reference.child("doctors").child(idDoctor).child("stateTo")
        viewModelScope.launch(Dispatchers.IO) {
            refStateTo.setValue(id)
        }
    }

    fun updateStateDoctorTyping(idDoctor: String) {
        val refState = DB.reference.child("doctors").child(idDoctor).child("state")
        viewModelScope.launch(Dispatchers.IO) {
            val data: String? = refState.get().await().getValue(String::class.java)
            if (data != null) {
                refState.setValue(typing)
//                updateStateTo(idDoctor, idDoctor)
            }
        }
    }

    private lateinit var refDoctorMEssages: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    fun removeListener() {
        refDoctorMEssages.removeEventListener(valueEventListener)
    }

    fun seenMessages(idDoctor: String, idPatient: String) {
        refDoctorMEssages = DB.reference.child("message").child(idDoctor).child(idPatient)
        val refDocSeen = DB.reference.child("message").child(idPatient).child(idDoctor)
        valueEventListener = refDoctorMEssages.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { messages ->
                        val data = messages.getValue(MessageData::class.java)!!
                        if (data.idFrom == idPatient) {
                            viewModelScope.launch(Dispatchers.IO) {
                                refDoctorMEssages.child(data.idMessage).child("seen").setValue("1")
                                refDocSeen.child(data.idMessage).child("seen").setValue("1")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private lateinit var refMess: DatabaseReference
    private lateinit var refMessEventListener: ValueEventListener
    fun removeListenerMess() {
        refMess.removeEventListener(refMessEventListener)
    }

    val messLiveData: MutableLiveData<MessageData> = MutableLiveData()

    fun getMessages(idDoctor: String, patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            refMess = DB.reference.child("message").child(idDoctor).child(patientId)
            refMessEventListener = refMess.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { messages ->
                            val data = messages.getValue(MessageData::class.java)
                            if (data != null) {
                                viewModelScope.launch {
                                    messLiveData.value = data!!
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
        }
    }
}