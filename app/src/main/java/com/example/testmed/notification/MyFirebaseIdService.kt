package com.example.testmed.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.example.testmed.*
import com.example.testmed.doctor.MainActivityDoctor
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
import android.media.RingtoneManager
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator


private const val CHANNEL_ID = "my_channel"

class MyFirebaseIdService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val fromId: String = remoteMessage.data["fromId"]!!
        val toId: String = remoteMessage.data["toId"]!!
        val body: String = remoteMessage.data["body"]!!
        val title: String = remoteMessage.data["title"]!!
        val notificationID: Int = remoteMessage.data["idNotification"]!!.toInt()
        val fromWho: String = remoteMessage.data["fromWho"]!!
        val notifyData = NotifyData(
            fromId, toId, body, title, notificationID, fromWho, 1
        )
//        Log.d("EVENT", fromId)
//        Log.d("EVENT", toId)
//        Log.d("EVENT", body)
//        Log.d("EVENT", title)
//        Log.d("EVENT", fromWho)
//
//        remoteMessage.data.forEach { event->
//            Log.d("EVENT", event.toString())
//            Log.d("EVENT", event.key.toString())
//            Log.d("EVENT", event.value.toString())
//        }

        if (AUTH().currentUser != null) {
            sendNotification(notifyData)
        }
    }

    private fun sendNotification(notifyData: NotifyData) {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId: Int = notifyData.idNotification
        Log.d("NOTINT", notificationId.toString())

        val pendingIntent: PendingIntent = if (notifyData.fromWho == "0") {
            NavDeepLinkBuilder(this)
                .setComponentName(MainActivityDoctor::class.java)
                .setGraph(R.navigation.mobile_navigation_doctor)
                .setDestination(R.id.navigation_chat_with_patient_fragment)
                .setArguments(bundleOf(ID_PATIENT to notifyData.fromId))
                .createPendingIntent()
        }else{
            NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.navigation_chat_with_doctor)
                .setArguments(bundleOf(ID_DOCTOR to notifyData.fromId))
                .createPendingIntent()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        notificationManager.activeNotifications.forEach {
            if (it.id == notificationId) {
                it.notification.visibility = Notification.VISIBILITY_PRIVATE
            }
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notifyData.title)
            .setContentText(notifyData.body)
            .setSmallIcon(R.drawable.ic_double_check_black_24)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 1000, 500, 1000))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
        playAudio(this, R.raw.anonymous_raw_gm)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val name = "channelName"
        val descriptionText = "My channel description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }


}

