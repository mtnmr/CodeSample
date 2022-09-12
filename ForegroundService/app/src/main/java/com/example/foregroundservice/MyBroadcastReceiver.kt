package com.example.foregroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intent = Intent(context, ForegroundService::class.java)
        context.stopService(intent)
        Log.d("service test", "stop")
    }
}