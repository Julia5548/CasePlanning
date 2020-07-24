package com.example.caseplanning.mainWindow


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.Notification.MyService
import com.example.caseplanning.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class MainWindowCasePlanning : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_window_case_planning)

        val intent = intent
        val uid = intent.getStringExtra("uid")
        val arg = Bundle()
        val windowTask = WindowTask()
        if(uid != null && uid != "") {
            arg.putString("uid", uid)
            windowTask.arguments = arg
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.linerLayout,
                windowTask
            )
            .commitAllowingStateLoss()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

    }

    @SuppressLint("SimpleDateFormat")
    private fun getTomorrowWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val tomorrowWeek = calendar.time.toString()
        val format = SimpleDateFormat("dd.MM.yyyy")
        val getDate = format.parse(tomorrowWeek)

        val week_format = SimpleDateFormat("EEEE")
        return week_format.format(getDate!!)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.getAction() == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_HOME -> startNotification(getTomorrowWeek())
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    @SuppressLint("SimpleDateFormat")
    private fun convertFormat(day: String): Date? {
        var date = day
        val arrayDate: List<String> = date.split(".")
        val month: String
        if (arrayDate[1].length == 1) {
            month = "0${arrayDate[1]}"
            date = "${arrayDate[0]}.$month.${arrayDate[2]}"
        }
        val dayTask: String
        if (arrayDate[0].length == 1) {
            dayTask = "0${arrayDate[0]}"
            date = "$dayTask.${arrayDate[1]}.${arrayDate[2]}"
        }
        val format = SimpleDateFormat("dd.MM.yyy")
        val formatedDay = format.parse(date)
        return formatedDay
    }

    private fun startNotification(tomorrowWeek: String) {
        val dataBase = DataBase()
        val user = FirebaseAuth.getInstance().currentUser!!
        val disposable = dataBase
            .retrieveData(user.uid)
            .subscribe {
                list->
                for(task in list){
                    if(task.replay == tomorrowWeek){
                        val intent = Intent(applicationContext, MyService::class.java)
                        val date = convertFormat(task.day)
                        val milliseconds = date!!.time
                        intent.putExtra("name_task", task.name)
                        intent.putExtra("time_notification", task.notification)
                        intent.putExtra("milliseconds", milliseconds)
                        startService(intent)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy")
//        finish()
//        exitProcess(0)
    }
}
