@file:Suppress("DEPRECATION")

package com.example.caseplanning.Notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        val milliseconds = intent!!.getStringExtra("time_notification")
        val name = intent.getStringExtra("name_task")
        sendNotification(name, milliseconds)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendNotification(name: String?, milliseconds: String?) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(this, MainWindowCasePlanning :: class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = Notification.Builder(this)
            .setAutoCancel(true)
            .setContentTitle(name)
            .setContentText("Сегодня $milliseconds")
            .setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentIntent(pendingIntent)
            .setSound(uri)
        val notification = builder.notification

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(42, notification)
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }
}