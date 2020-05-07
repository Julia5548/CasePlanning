package com.example.caseplanning.mainWindow

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R
import org.w3c.dom.Text

class Timer(var time: String) : Fragment() {

    private var chronometer: Chronometer? = null
    private var mediaPlayer : MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.start_timer, null, false)

        chronometer = view.findViewById<Chronometer>(R.id.chronometer)

        ButterKnife.bind(this, view)
        return view
    }

    @OnClick(R.id.startTimer)
    fun startTimer() {
        val startTimer = view!!.findViewById<TextView>(R.id.startTimer)
        val stopTimer = view!!.findViewById<TextView>(R.id.stopTimer)

        startTimer.visibility = TextView.INVISIBLE
        stopTimer.visibility = TextView.VISIBLE

        chronometer!!.base = SystemClock.elapsedRealtime()
        chronometer!!.start()

        chronometer!!.setOnChronometerTickListener { chronometer ->
            if (time != "") {
                if (chronometer.text.toString() == time) {
                    releasePlay()
                    mediaPlayer = MediaPlayer.create(context, RingtoneManager.getActualDefaultRingtoneUri(context, 1))
                    mediaPlayer!!.start()
                   // stopTimer.performClick()
                }
            }
        }
    }

    private fun releasePlay() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


    @OnClick(R.id.stopTimer)
    fun stopTimer() {
        val startTimer = view!!.findViewById<TextView>(R.id.startTimer)
        val stopTimer = view!!.findViewById<TextView>(R.id.stopTimer)

        startTimer.visibility = TextView.VISIBLE
        stopTimer.visibility = TextView.INVISIBLE

        chronometer!!.stop()
        chronometer!!.base = SystemClock.elapsedRealtime()
        if (mediaPlayer != null)
            mediaPlayer!!.stop()
    }
}