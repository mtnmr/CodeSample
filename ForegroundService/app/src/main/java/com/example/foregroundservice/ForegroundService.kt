package com.example.foregroundservice

import android.app.*
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ForegroundService:Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        val stopIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = ACTION_SEND
        }
        val stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("foreground service")
            .setContentText("foreground service running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE) //通知をすぐに表示する
            .addAction(R.drawable.ic_launcher_foreground, "STOP", stopPendingIntent)
            .build()

        Log.d("service test", "start")

//        Thread(
//            Runnable {
//                Log.d("service test", "start")
//                (0..5).map {
//                    Thread.sleep(1000)
//
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    stopForeground(Service.STOP_FOREGROUND_REMOVE)
//                    Log.d("service test", "finish A")
//                }else{
//                    stopForeground(true)
//                    Log.d("service test", "finish B")
//                }
//
//            }).start()


        startForeground(1, notification)

        return START_STICKY
    }
}