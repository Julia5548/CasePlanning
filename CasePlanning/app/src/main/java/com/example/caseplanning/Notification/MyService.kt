package com.example.caseplanning.Notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.caseplanning.DataBase.Task
import java.util.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class MyService : Service() {

    var TAG = "Timers"
    private var task: Task? = null
    private var milliseconds: Long? = 0
    val list_notification = arrayListOf<String>()
    val NOTIFICATION_ID_BIG_IMAGE = 101

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("ShortAlarm")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        task = intent?.getSerializableExtra("task") as Task
        milliseconds = intent.getLongExtra("milliseconds", 0)

        val intentNotification = Intent(applicationContext!!, NotificationBroadcast::class.java)
        intentNotification.putExtra("task", task)
        intentNotification.putExtra("list", list_notification)

        val randomValues =
            NOTIFICATION_ID_BIG_IMAGE + ((Date().time / 1000L % Int.MAX_VALUE).toInt())
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext!!,
            randomValues,
            intentNotification,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager: AlarmManager =
            applicationContext!!.getSystemService(ALARM_SERVICE) as AlarmManager

        Log.d(TAG, "millisecond $milliseconds")
        if (milliseconds != null && task!!.replay == "Нет >") {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, milliseconds!!, pendingIntent)
            Log.d(TAG, "AlarmManager")
        }
        if (milliseconds != null && task!!.replay != "Нет >")
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, milliseconds!!, AlarmManager.INTERVAL_DAY, pendingIntent)
        Log.d(TAG, "End onStartCommand")


        return START_REDELIVER_INTENT
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}