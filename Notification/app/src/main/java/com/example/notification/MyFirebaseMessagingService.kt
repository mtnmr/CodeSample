package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService:FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title.toString()
        val body = message.notification?.body.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FCM"
            val descriptionText = "firebase cloud messaging"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val id = getString(R.string.fcm_notification_channel_id)
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, getString(R.string.fcm_notification_channel_id))
            .setSmallIcon(R.drawable.ic_baseline_icecream_24)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "notification received in foreground"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val uuid = UUID.randomUUID().hashCode()
        notificationManager.notify(uuid, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}