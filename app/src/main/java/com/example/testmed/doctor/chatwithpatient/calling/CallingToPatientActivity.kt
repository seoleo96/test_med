package com.example.testmed.doctor.chatwithpatient.calling

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testmed.CONSULTING
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.model.ConsultingData
import com.facebook.react.modules.core.PermissionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.*


class CallingToPatientActivity : JitsiMeetActivity() {
    private lateinit var view: JitsiMeetView
    private lateinit var patientId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        val consultingLinc = intent.getStringExtra("consultingLinc")
        patientId = intent.getStringExtra("patientId").toString()
        view = JitsiMeetView(this)
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(consultingLinc)
            .setAudioMuted(false)
            .setAudioOnly(false)
            .setVideoMuted(false)
            .build()
        view.join(options)
        setContentView(view)
    }

    override fun onStop() {
        super.onStop()
        if (patientId != "null" ){
            lifecycleScope.launch(Dispatchers.IO) {
                val refConsulting = "online_consulting/${UID()}/${patientId}/status"
                DB.reference.child(refConsulting).setValue("offline")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        CONSULTING = "NOT_CONSULTING"
        view.leave()

    }
}