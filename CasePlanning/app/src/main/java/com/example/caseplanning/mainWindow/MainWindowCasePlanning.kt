package com.example.caseplanning.mainWindow


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.caseplanning.R


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
}
