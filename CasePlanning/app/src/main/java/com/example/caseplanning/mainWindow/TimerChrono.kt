package com.example.caseplanning.mainWindow

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R

class TimerChrono(time: String) : Fragment() {

    private var chronometer: Chronometer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var mTime = time

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.start_timer, null)

        chronometer = view.findViewById(R.id.chronometer)
        chronometer!!.isCountDown = true

        val pro = convertTime()
        chronometer!!.base = SystemClock.elapsedRealtime() + convertTime()

        ButterKnife.bind(this, view)

        return view
    }

    private fun convertTime(): Long {
        val arrayDate: List<String> = mTime.split(":")
        val milliseconds : Long
        if (mTime.length == 8) {

            var hours: Long = if (arrayDate[0][0] == '0') {
                Character.getNumericValue(arrayDate[0][1]).toLong()
            } else {
                arrayDate[0].toLong()
            }
            var minute: Long = if (arrayDate[1][0] == '0') {
                Character.getNumericValue(arrayDate[1][1]).toLong()
            } else {
                arrayDate[1].toLong()
            }
            var seconds: Long = if (arrayDate[2][0] == '0') {
                Character.getNumericValue(arrayDate[2][1]).toLong()
            } else {
                arrayDate[2].toLong()
            }
            hours *= 60
            minute += hours
            minute *= 60
            seconds += minute
            milliseconds = seconds * 1000
        }else{

            var minute: Long = if (arrayDate[0][0] == '0') {
                Character.getNumericValue(arrayDate[0][1]).toLong()
            } else {
                arrayDate[0].toLong()
            }
            var seconds: Long = if (arrayDate[1][0] == '0') {
                Character.getNumericValue(arrayDate[1][1]).toLong()
            } else {
                arrayDate[1].toLong()
            }

            minute *= 60
            seconds += minute

            milliseconds = seconds * 1000
        }

        return milliseconds
    }

    @OnClick(R.id.startTimer)
    fun startTimer() {
        val startTimer = view!!.findViewById<TextView>(R.id.startTimer)
        val stopTimer = view!!.findViewById<TextView>(R.id.stopTimer)

        startTimer.visibility = TextView.INVISIBLE
        stopTimer.visibility = TextView.VISIBLE

        chronometer!!.base = SystemClock.elapsedRealtime() + convertTime()
        chronometer!!.start()

        chronometer!!.setOnChronometerTickListener { chronometer ->
            if (chronometer.text.toString() == "00:00") {
                releasePlay()
                mediaPlayer = MediaPlayer.create(
                    context,
                    RingtoneManager.getActualDefaultRingtoneUri(context, 1)
                )
                mediaPlayer!!.start()
                // stopTimer.performClick()
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
        chronometer!!.base = SystemClock.elapsedRealtime() + convertTime()

        if (mediaPlayer != null)
            mediaPlayer!!.stop()
    }

    @OnClick(R.id.closeTimer)
    fun closeTimer() {
        chronometer!!.stop()
        if (mediaPlayer != null)
            mediaPlayer!!.stop()
        fragmentManager?.popBackStack()
    }
}