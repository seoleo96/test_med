package com.example.testmed.doctor.chatwithpatient.calling

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.PermissionListener
import org.jitsi.meet.sdk.*


class CallingToPatientActivity : JitsiMeetActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = JitsiMeetView(this)
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom("https://meet.jit.si/test123")
            .setAudioMuted(false)
            .setAudioOnly(false)
            .setVideoMuted(false)
            .build()
        view.join(options)
        setContentView(view)
    }




}