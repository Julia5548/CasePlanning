package com.example.caseplanning.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationBroadcast : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action.equals("android.intent.action.BOOT_COMPLETED")){
            val serviceIntent = Intent(context!!, NotificationService::class.java)
            val time_notification = intent.getStringExtra("time_notification")
            val name = intent.getStringExtra("name_task")

            serviceIntent.putExtra("name_task", name)
            serviceIntent.putExtra("time_notification", time_notification)

         //   context.stopService(serviceIntent)
            context.startService(serviceIntent)
        }else{
            Log.d("BrodcastLog", "Alarm Manager just ran")
        }
    }
}