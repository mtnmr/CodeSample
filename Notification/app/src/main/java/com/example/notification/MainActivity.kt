package com.example.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

const val CHANNEL_ID_REGULAR = "notification Regular Intent"
const val notificationRegularId = 1

const val CHANNEL_ID_SPECIAL = "notification Special Intent"
const val notificationSpecialId = 2

const val CHANNEL_ID_PROGRESS = "notification progress"
const val notificationProgressId = 3

const val KEY_TEXT_REPLY = "key_text_reply"

const val NOTIFICATION_GROUP = "notification group"
const val summeryId = 4

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
            createSummeryNotification()
        }

        val specialIntentButton = findViewById<Button>(R.id.notification_special_button)
        specialIntentButton.setOnClickListener {
            createNotificationIntentSpecial()
        }

        val progressButton = findViewById<Button>(R.id.notification_progress_button)
        progressButton.setOnClickListener {
            createNotificationProgress()
        }
    }

    /*
    通知をタップするとActivityが開く。戻るボタンでMainActivityに戻る。
    Manifestに設定を追記する必要あり。
     */
    private fun createNotificationIntentRegular() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.regular_activity)
            val descriptionText = getString(R.string.channel_description_regular)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_REGULAR, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //通知タップしてActivityを起動
        val regularIntent = Intent(this, RegularActivity::class.java)
        val regularPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
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
            NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "REPLY", replyIntent
            )
                .addRemoteInput(remoteInput)
                .build()


        val builder = NotificationCompat.Builder(this, CHANNEL_ID_REGULAR)
            .setSmallIcon(R.drawable.ic_baseline_cake_24)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("regular intent ")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "regular intent" +
                                "Much longer text that cannot fit one line..."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(regularPendingIntent)
//            .addAction(R.drawable.ic_baseline_cake_24, "Action", actionPendingIntent) //アクションボタンの追加
            .addAction(action)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationRegularId, builder.build())
    }

    /*
    戻るボタンは表示されず、単独のActivityとして起動する。
    Manifestに設定を追記する必要あり。
     */
    private fun createNotificationIntentSpecial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.special_activity)
            val descriptionText = getString(R.string.channel_description_special)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_SPECIAL, name, importance).apply {
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
            this,
            0,
            specialIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_SPECIAL)
            .setSmallIcon(R.drawable.ic_baseline_catching_pokemon_24)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("special intent")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "special intent " +
                                "Much longer text that cannot fit one line..."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(specialPendingIntent)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)


        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationSpecialId, builder.build())
    }

    /*
    RegularとSpecial両方通知したとき、通知を１つにグループにまとめる。
    展開すると各通知ごとに操作することができる。
    グループでまとめる時の概要を表示する通知を作成。
    この関数はRegularのClickListenerに追加した。Regularが1件通知された場合はグループ表示はされない。
     */
    private fun createSummeryNotification(){
        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID_SPECIAL)
            .setContentTitle("Summary")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setGroup(NOTIFICATION_GROUP)
            .setGroupSummary(true)
            .build()

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(summeryId, summaryNotification)
    }

    /*
    進捗状況を表示する
     */
    private fun createNotificationProgress() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.progress_show)
            val descriptionText = getString(R.string.channel_description_progress)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_PROGRESS, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_PROGRESS)
            .setContentTitle("Download Task")
            .setContentText("Downloading your file...")
            .setSmallIcon(R.drawable.ic_baseline_info_24)

        val max = 10
        var progress = 0
        var percentage = 0

        with(NotificationManagerCompat.from(this)) {
            builder.setProgress(max, progress, true)
            notify(notificationProgressId, builder.build())

            Thread(Runnable {
                while (progress < max) {
                    progress += 1

                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    if (progress == max) {
                        builder.setContentText("Download complete.")
                        builder.setProgress(0, 0, false)
                    } else {
                        percentage = (progress * 100) / max
                        builder.setContentText("$percentage% complete $progress of $max")
                        builder.setProgress(max, progress, true)
                    }

                    notify(notificationProgressId, builder.build())

                }
            }).start()
        }
    }
}