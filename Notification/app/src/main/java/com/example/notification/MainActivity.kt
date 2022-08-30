package com.example.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

const val CHANNEL_ID_Regular = "notification Regular Intent"
const val CHANNEL_ID_Special = "notification Special Intent"
const val notificationRegularId = 1
const val notificationSpecialId = 2

const val KEY_TEXT_REPLY = "key_text_reply"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        通知チャンネルごとにデバイスで通知設定ができる。
        この書き方だとボタンを押したときにしか通知チャンネルが作成されないため、
        通常はアプリを起動したらまずcreateNotificationChannelをする。
         */

        val regularIntentButton = findViewById<Button>(R.id.notification_regular_button)
        regularIntentButton.setOnClickListener {
            createNotificationIntentRegular()
        }

        val specialIntentButton = findViewById<Button>(R.id.notification_special_button)
        specialIntentButton.setOnClickListener {
            createNotificationIntentSpecial()
        }
}

    /*
    通知をタップするとActivityが開く。戻るボタンでMainActivityに戻る。
    Manifestに設定を追記する必要あり。
     */
    private fun createNotificationIntentRegular(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.regular_activity)
            val descriptionText = getString(R.string.channel_description_regular)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_Regular, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //通知タップしてActivityを起動
        val regularIntent = Intent(this, RegularActivity::class.java)
        val regularPendingIntent:PendingIntent? = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(regularIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        /*
        //アクションボタンを追加
        val actionIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val actionPendingIntent:PendingIntent =
            PendingIntent.getActivity(
                this, 0, actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

         */

        //返信ボタン
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel("reply")
            build()
        }

        val replyIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, ReplyReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,
                "REPLY", replyIntent)
                .addRemoteInput(remoteInput)
                .build()


        val builder = NotificationCompat.Builder(this, CHANNEL_ID_Regular)
            .setSmallIcon(R.drawable.ic_baseline_cake_24)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("regular intent ")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("regular intent" +
                        "Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(regularPendingIntent)
//            .addAction(R.drawable.ic_baseline_cake_24, "Action", actionPendingIntent) //アクションボタンの追加
            .addAction(action)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationRegularId, builder.build())
    }

    /*
    戻るボタンは表示されず、単独のActivityとして起動する。
    Manifestに設定を追記する必要あり。
     */
    private fun createNotificationIntentSpecial(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.special_activity)
            val descriptionText = getString(R.string.channel_description_special)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_Special, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val specialIntent = Intent(this, SpecialActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val specialPendingIntent = PendingIntent.getActivity(
            this, 0, specialIntent,  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_Special)
            .setSmallIcon(R.drawable.ic_baseline_catching_pokemon_24)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("special intent")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("special intent " +
                        "Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(specialPendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationSpecialId, builder.build())
    }

}