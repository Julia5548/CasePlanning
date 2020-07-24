package com.example.caseplanning.Notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.MainWindowCasePlanning
import java.util.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class NotificationBroadcast : BroadcastReceiver() {

    val NOTIFICATION_ID_BIG_IMAGE = 101
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {

        val task = intent?.getSerializableExtra("task") as Task
        val resultIntent = Intent(context, MainWindowCasePlanning::class.java)
        val randomValues = NOTIFICATION_ID_BIG_IMAGE + ((Date().time / 1000L % Int.MAX_VALUE).toInt())
        val resultPendingIntent = PendingIntent.getActivity(context, randomValues, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(task.name)
            .setSound(uri)
            .setContentIntent(resultPendingIntent)
            .setContentText("Сегодня ${task.notification}")
            .setAutoCancel(true)
            .build()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BIG_IMAGE + ((Date().time / 1000L % Int.MAX_VALUE).toInt()), notification)

        val intentNotification = Intent(context, MyService::class.java)

        context.stopService(intentNotification)

    }
}