@file:Suppress("DEPRECATION")

package com.example.caseplanning.Notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.MainWindowCasePlanning
import java.util.concurrent.TimeUnit


class NotificationService : Service() {

    var TAG = "Timers"
    private lateinit var notificationManager: NotificationManager

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        val time_notification = intent!!.getStringExtra("time_notification")
        val name = intent.getStringExtra("name_task")
        val milliseconds = intent.getLongExtra("milliseconds", 0)

        val intentNotification = Intent(applicationContext!!, NotificationBroadcast::class.java)
        intentNotification.putExtra("name_task", name)
        intentNotification.putExtra("time_notification", time_notification)

        val pendingIntent = PendingIntent.getBroadcast(applicationContext!!, 42, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager : AlarmManager = applicationContext!!.getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }
}