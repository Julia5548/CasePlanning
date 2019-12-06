package com.example.caseplanning


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainWindowCasePlanning : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_window_case_planning)

        supportFragmentManager.beginTransaction()
            .add(R.id.linerLayout, WindowTask())
            .commit()
    }



}
