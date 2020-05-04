package com.example.caseplanning.mainWindow

import android.app.Dialog
import android.app.FragmentTransaction
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.PhotoEdit
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.task_window.*
import java.lang.Exception

class FragmentDialog(val dataTask: Task?) : DialogFragment() {

    private var mediaPlayer: MediaPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(context).inflate(R.layout.card_task, null, false)

        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        createdView(view)
        return MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton("Ok", null)
            .create()
    }

    private fun createdView(view: View) {


        val nameTask = view.findViewById<TextView>(R.id.nameTask)
        val periodDay = view.findViewById<TextView>(R.id.periodDay)
        val date = view.findViewById<TextView>(R.id.day)
        val replayDay = view.findViewById<TextView>(R.id.replay_day)
        val listSubTask = view.findViewById<RecyclerView>(R.id.recyclerViewSubTask)
        val timer = view.findViewById<TextView>(R.id.loadTimer)
        val startTimer = view.findViewById<TextView>(R.id.startTimer)
        val comment = view.findViewById<TextView>(R.id.comment)
        var subTasks = arrayListOf<String>()
        var photo: ImageView? = null
        val video = view.findViewById<VideoView>(R.id.videoView)
        val playAudio = view.findViewById<ImageButton>(R.id.startAndStopPlay)
        val chronometer = view.findViewById<Chronometer>(R.id.chronometerAudio)

        if (dataTask != null) {

            nameTask.text = dataTask.name

            if (dataTask.period != "")
                periodDay.text = dataTask.period
            date.text = dataTask.day

            if (dataTask.replay != "Нет >")
                replayDay.text = dataTask.replay
            if (dataTask.listSubTasks!!.size != 0) {
                listSubTask.layoutManager = LinearLayoutManager(context)
                for (subTask in dataTask.listSubTasks!!) {
                    subTasks.add(subTask)
                }

                listSubTask.adapter = AdapterRecyclerView(context!!, subTasks)
                subTasks = arrayListOf()
                if (view.parent != null) {
                    (view.parent as ViewGroup).removeView(view)
                }
            }
            if (dataTask.timer != "Указать >") {
                timer.text = dataTask.timer
            } else {
                startTimer.isEnabled = false
            }
            if (dataTask.comment != "")
                comment.text = dataTask.comment
            if (dataTask.photo != "") {

                if (photo != null) {
                    (photo.drawable as? BitmapDrawable)!!.bitmap.recycle()
                }
                photo = view.findViewById<ImageButton>(R.id.imageViewPhoto)
                photo!!.visibility = ImageButton.VISIBLE


                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                options.inSampleSize = 32
                val bitmap = BitmapFactory.decodeFile(dataTask.photo, options)
                photo.setImageBitmap(bitmap)
            }

            if (dataTask.video != "") {
                video.visibility = VideoView.VISIBLE
                video.setVideoURI(dataTask.video!!.toUri())
            }
            if (dataTask.audio != "" && dataTask.timeAudio == ""){

                playAudio.visibility = ImageButton.VISIBLE
                chronometer.visibility = Chronometer.VISIBLE

                playAudio.setOnClickListener {

                    playAudio(chronometer, dataTask.timeAudio!!, dataTask.audio!!)
                }



            }
        }


    }

    /*воспроизведение и остновка аудио*/
    fun playAudio(mChronometer:Chronometer, timeAudio:String, fileName : String) {

        val playAndStopAudio = view!!.findViewById<ImageButton>(R.id.startAndStopPlay)
        try {

            var time = timeAudio
            if (playAndStopAudio.tag == null) {
                releasePlay()
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(fileName)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                mChronometer.base = SystemClock.elapsedRealtime()
                mChronometer.start()
                playAndStopAudio.setImageResource(R.drawable.ic_stop_black_24dp)
                playAndStopAudio.tag = 1

                mChronometer.setOnChronometerTickListener {
                        chronometer ->
                    if (time != "") {
                        if (chronometer.text.toString() == time) {
                            chronometer.stop()
                            time = ""
                            stopPlayAudio(time, mChronometer)
                            playAndStopAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                            playAndStopAudio.tag = null
                        }
                    }
                }

            } else if (playAndStopAudio.tag == 1) {
                stopPlayAudio(time, mChronometer)
                mChronometer.stop()
                mChronometer.base = SystemClock.elapsedRealtime()
                playAndStopAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                playAndStopAudio.tag = null
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releasePlay() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }

    }

    /*остановка воспроизведение аудио*/
    fun stopPlayAudio(timeAudio: String, chronometer: Chronometer) {

        if (mediaPlayer != null)
            mediaPlayer!!.stop()

        if(timeAudio == ""){
            chronometer.base = SystemClock.elapsedRealtime()
        }

    }

    @OnClick(R.id.startTimer)
    fun startTimer(){
        fragmentManager!!.beginTransaction().replace(R.id.linerLayout, Timer()).addToBackStack(null).commit()
    }

}