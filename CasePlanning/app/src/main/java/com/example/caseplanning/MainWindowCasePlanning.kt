package com.example.caseplanning


import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction


@Suppress("DEPRECATION")
class MainWindowCasePlanning : AppCompatActivity() {

    private var fragmentTransaction : FragmentTransaction?  = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_window_case_planning)

    }

    override fun onPostResume() {
        if (supportFragmentManager.findFragmentById(R.id.linerLayout) != null)
        {
            supportFragmentManager
                .beginTransaction()
                .remove(
                    supportFragmentManager
                        .findFragmentById(R.id.linerLayout)!!
                )
                .commit()
        }
            supportFragmentManager.beginTransaction()
                .add(R.id.linerLayout, WindowTask())
                .commitAllowingStateLoss()

        super.onPostResume()
    }
}
