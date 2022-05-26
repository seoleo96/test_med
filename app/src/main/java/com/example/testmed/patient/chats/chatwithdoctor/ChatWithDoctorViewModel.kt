package com.example.testmed.patient.chats.chatwithdoctor

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.*
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.example.testmed.model.MessageData
import com.example.testmed.notification.NotifyData
import com.example.testmed.notification.RetrofitClient
import com.example.testmed.notification.Sender
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ChatWithDoctorViewModel : ViewModel() {

    val livedata: MutableLiveData<DoctorData> = MutableLiveData()

    fun getDoctorsData(idDoc: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DB.reference
                .child("doctors").child(idDoc)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(DoctorData::class.java)
                            viewModelScope.launch {
                                livedata.value = data!!
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }

    }

    fun saveMainList(text: String, timestamp: Any, type: String, idDoc: String, docImUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val refPatient = "main_list/${UID()}/$idDoc"
            val refDoctor = "main_list/$idDoc/${UID()}"
            val childPatient = CommonPatientData(
                id = idDoc,
                photoUrl = docImUrl,
                message = text,
                timestamp = timestamp,
                type = type
            )
            val childDoctor = CommonPatientData(
                id = UID(),
                photoUrl = docImUrl,
                message = text,
                timestamp = timestamp,
                type = type
            )
            DB.reference.child(refPatient).setValue(childPatient)
            DB.reference.child(refDoctor).setValue(childDoctor)
        }
    }

    fun sendNotificationData(
        idDoc: String,
        idPatient: String,
        message: String,
        idNotification: Int,
        type: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val reDoctorToken = DB.reference.child("doctors").child(idDoc).child("token")
            val refUserToken = DB.reference.child("patients").child(idPatient).child("token")
            val refUserName = DB.reference.child("patients").child(idPatient).child("name")
            val refUserSurname = DB.reference.child("patients").child(idPatient).child("surname")
            val name = refUserName.get().await().getValue(String::class.java)
            val surname = refUserSurname.get().await().getValue(String::class.java)
            val titleName = "$name $surname"
            val notifyData = NotifyData(
                fromId = UID(),
                title = titleName,
                icon = if (type != "message" && type != "image") 100 else 1,
                body = message,
                idNotification = idNotification,
                fromWho = "0",
                toId = idDoc,
                type = type
            )
            val tokenDoc = reDoctorToken.get().await().getValue(String::class.java)
            val tokenPat = refUserToken.get().await().getValue(String::class.java)
            Log.d("TOCENDOC", "$tokenDoc")
            Log.d("TOCENPAT", "$tokenPat")
            val sender = Sender(notifyData, tokenDoc!!)
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

    fun sendMessage(idMess: String, text: String, type: String, timestamp: Any, idDoc: String, idNotification: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val idPatient = UID()
            val message = MessageData(idMess, idPatient, idDoc, text, timestamp, type, "0", idNotification)
            var refPatient = ""
            var refDoctor = ""
            refPatient = "message/$idPatient/$idDoc"
            refDoctor = "message/$idDoc/$idPatient"
            DB.reference.child(refPatient).child(idMess).setValue(message)
            DB.reference.child(refDoctor).child(idMess).setValue(message)
        }
    }

    fun updateStateTo(id: String) {
        val refStateTo = DB.reference.child("patients").child(UID()).child("stateTo")
        viewModelScope.launch(Dispatchers.IO) {
            refStateTo.setValue(id)
        }
    }

    fun updateTyping(idDoctor: String) {
        val refState = DB.reference.child("patients").child(UID()).child("state")
        viewModelScope.launch(Dispatchers.IO) {
            val data = refState.get().await().getValue(Any::class.java)
            if (data != null) {
                refState.setValue(typing)
                updateStateTo(idDoctor)
            }
        }
    }

    private lateinit var refPatientMEssages: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    fun removeListener() {
        refPatientMEssages.removeEventListener(valueEventListener)
    }

    fun seenMessages(
        idDoctor: String,
        idPatient: String,
    ) {
        refPatientMEssages = DB.reference.child("message").child(idPatient).child(idDoctor)
        val refDoc = DB.reference.child("message").child(idDoctor).child(idPatient)
        viewModelScope.launch(Dispatchers.IO) {
            valueEventListener =
                refPatientMEssages.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { dataSnapshot ->
                                val data = dataSnapshot.getValue(MessageData::class.java)!!
                                if (data.idFrom == idDoctor) {
                                    refPatientMEssages.child(data.idMessage).child("seen")
                                        .setValue("1")
                                    refDoc.child(data.idMessage).child("seen").setValue("1")

                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    fun updateToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val ref = DB.reference.child("patients").child(UID()).child("token")
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    ref.setValue(token)
                }
        }
    }

    private lateinit var refMess: DatabaseReference
    private lateinit var refMessEventListener: ValueEventListener
    fun removeListenerMess() {
        refMess.removeEventListener(refMessEventListener)
    }

    val messLiveData: MutableLiveData<MessageData> = MutableLiveData()

    fun getMessages(idDoctor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            refMess = DB.reference.child("message").child(UID()).child(idDoctor)
            refMessEventListener = refMess.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val data = it.getValue(MessageData::class.java)
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