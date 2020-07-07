package com.example.caseplanning.Notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.MainWindowCasePlanning

class NotificationBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val name_task = intent!!.getStringExtra("name_task")
        val timeNotification = intent.getStringExtra("time_notification")
        val resultIntent = Intent(context, MainWindowCasePlanning::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(name_task)
            .setSound(uri)
            .setContentIntent(resultPendingIntent)
            .setContentText("Сегодня $timeNotification")
            .setAutoCancel(true)
            .build()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(42, notification)

        val intentNotification = Intent(context, NotificationService::class.java)
        context.stopService(intentNotification)

    }
}