package com.example.testmed.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
        val icon: String = remoteMessage.data["icon"]!!
        val notificationID: Int = remoteMessage.data["idNotification"]!!.toInt()
        val fromWho: String = remoteMessage.data["fromWho"]!!
        val type: String = remoteMessage.data["type"]!!
        val notifyData = NotifyData(
            fromId, toId, body, title, notificationID, fromWho, icon.toInt(), type
        )
        Log.d("EVENT", fromId)
        Log.d("EVENT", toId)
        Log.d("EVENT", body)
        Log.d("EVENT", title)
        Log.d("EVENT", fromWho)
        Log.d("EVENT", icon)
        Log.d("EVENT", type)
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

        Log.d("EVENT", notifyData.toString())

        var notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId: Int = notifyData.idNotification
        Log.d("NOTINT", notificationId.toString())

        val pendingIntent: PendingIntent = when {
            notifyData.body.contains("https://meet.jit.si/") -> {
                CONSULTING = "CONSULTING"
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.navigation_chat_with_doctor)
                    .setArguments(bundleOf(ID_DOCTOR to notifyData.fromId,
                        CONSULTING to "CONSULTING"))
                    .createPendingIntent()
            }

            notifyData.icon == 100 && notifyData.fromWho == "0" && notifyData.type != "image" && notifyData.type != "message" -> {
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivityDoctor::class.java)
                    .setGraph(R.navigation.mobile_navigation_doctor)
                    .setDestination(R.id.navigation_chat_with_patient_fragment)
                    .setArguments(bundleOf(ID_PATIENT to notifyData.fromId))
                    .createPendingIntent()
            }
            notifyData.icon == 101 && notifyData.fromWho == "1" && notifyData.type != "image" && notifyData.type != "message" -> {
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.navigation_chat_with_doctor)
                    .setArguments(bundleOf(ID_DOCTOR to notifyData.fromId))
                    .createPendingIntent()
            }
            notifyData.fromWho == "0" && notifyData.icon == 1 -> {
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivityDoctor::class.java)
                    .setGraph(R.navigation.mobile_navigation_doctor)
                    .setDestination(R.id.navigation_chat_with_patient_fragment)
                    .setArguments(bundleOf(ID_PATIENT to notifyData.fromId))
                    .createPendingIntent()
            }

            notifyData.icon == 2 && notifyData.fromWho == "0" -> {
                Log.d("NOTIFY", notifyData.idNotification.toString())
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivityDoctor::class.java)
                    .setGraph(R.navigation.mobile_navigation_doctor)
                    .setDestination(R.id.navigation_home_doctor)
                    .setArguments(bundleOf(ID_PATIENT to "consulting"))
                    .createPendingIntent()
            }
            notifyData.fromWho == "1" && notifyData.icon == 1 -> {
                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.navigation_chat_with_doctor)
                    .setArguments(bundleOf(ID_DOCTOR to notifyData.fromId))
                    .createPendingIntent()
            }

            else -> {
                NavDeepLinkBuilder(this)
                    .createPendingIntent()
            }
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
            .setContentText(
                when (notifyData.type) {
                    "image" -> "????????"
                    "message" -> notifyData.body
                    "payment" -> notifyData.title
                    else -> "????????"
                }
            )
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

