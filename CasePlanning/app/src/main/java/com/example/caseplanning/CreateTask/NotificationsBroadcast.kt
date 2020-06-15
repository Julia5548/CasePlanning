package com.example.caseplanning.CreateTask

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.MainWindowCasePlanning
import java.text.SimpleDateFormat

class NotificationsBroadcast() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val name_task = intent!!.getStringExtra("name_task")
        val timeNotification = intent.getStringExtra("time_notification")
        val resultIntent = Intent(context, MainWindowCasePlanning::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(name_task)
            .setSound(uri)
            .setContentIntent(resultPendingIntent)
            .setContentText("Сегодня $timeNotification")
            .build()

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(42, builder)
    }
}