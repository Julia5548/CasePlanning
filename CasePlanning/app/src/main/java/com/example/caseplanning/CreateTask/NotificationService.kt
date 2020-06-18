package com.example.caseplanning.CreateTask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.MainWindowCasePlanning
import java.util.*


class NotificationService /*: Service()*/ {
    /*val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    private lateinit var name_task : String
    private lateinit var timeNotification : String
    private lateinit var resultPendingIntent : PendingIntent
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"

    var Your_X_SECS = 5
    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        name_task = intent!!.getStringExtra("name_task")!!
        timeNotification = intent.getStringExtra("time_notification")!!
        val resultIntent = Intent(applicationContext, MainWindowCasePlanning::class.java)
        resultPendingIntent = PendingIntent.getActivity(applicationContext, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        startTimer()
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stopTimerTask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler: Handler = Handler()
    fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000) //
    }

    fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post(Runnable { createNotification() })
            }
        }
    }

    private fun createNotification() {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder =
            NotificationCompat.Builder(
                applicationContext,
                default_notification_channel_id
            )
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        mBuilder.setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(name_task)
            .setSound(uri)
            .setContentIntent(resultPendingIntent)
            .setContentText("Сегодня $timeNotification")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            if (false) {
                error("Assertion failed")
            }
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }*/
}