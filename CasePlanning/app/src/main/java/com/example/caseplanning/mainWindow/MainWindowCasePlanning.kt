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

        supportFragmentManager.beginTransaction()
            .add(
                R.id.linerLayout,
                WindowTask()
            )
            .commitAllowingStateLoss()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

    }
}
