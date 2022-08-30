package com.example.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class ReplyReceiver:BroadcastReceiver() {

    private var replyText: CharSequence ?= null

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            replyText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)
        }

        context?.apply {
            val repliedNotification = NotificationCompat.Builder(context, CHANNEL_ID_Regular)
            .setSmallIcon(R.drawable.ic_baseline_cake_24)
            .setContentText(replyText.toString())
            .build()

            //replyできたことを通知するために同じIDで通知を更新
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationRegularId, repliedNotification)
        }

    }
}