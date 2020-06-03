package com.example.caseplanning.mainWindow

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerViewFolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.disposables.Disposable
import java.lang.Exception

class FragmentDialog(
    val dataTask: Task?,
    disposable: Disposable?
) : DialogFragment(), DialogInterface.OnClickListener {

    private var mediaPlayer: MediaPlayer? = null
    private var time: String = ""
    private val mDisposable = disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(context).inflate(R.layout.card_task, null, false)
        ButterKnife.bind(this, view)
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme)
            .setView(view)
            .setPositiveButton("Ok", this)
            .setNeutralButton("Открыть таймер", this)
            .create()
        createdView(view, materialAlertDialogBuilder)

        return materialAlertDialogBuilder
    }


    override fun onClick(dialog: DialogInterface?, which: Int) {

        when(which){
            Dialog.BUTTON_NEUTRAL->{
                fragmentManager!!.beginTransaction().replace(R.id.linerLayout, Timer(time)).addToBackStack(null).commit()
                dialog!!.dismiss()
            }
            Dialog.BUTTON_POSITIVE -> {
                dialog!!.dismiss()

                if(mDisposable != null && !mDisposable.isDisposed)
                    mDisposable.dispose()
            }
        }
    }
    private fun createdView(
        view: View,
        dialog: AlertDialog
    ) {
        val nameTask = view.findViewById<TextView>(R.id.nameTask)
        val periodDay = view.findViewById<TextView>(R.id.periodDay)
        val date = view.findViewById<TextView>(R.id.day)
        val replayDay = view.findViewById<TextView>(R.id.replay_day)
        val listSubTask = view.findViewById<RecyclerView>(R.id.recyclerViewSubTask)
        val timer = view.findViewById<TextView>(R.id.loadTimer)
        val comment = view.findViewById<TextView>(R.id.contentComment)
        var subTasks = arrayListOf<String>()
        var photo: ImageView? = null
        val video = view.findViewById<VideoView>(R.id.videoView)
        val photoText = view.findViewById<TextView>(R.id.photoText)
        val videoText = view.findViewById<TextView>(R.id.videoText)
        val audioText = view.findViewById<TextView>(R.id.textAudio)
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

                listSubTask.adapter = AdapterRecyclerViewFolder(context!!, subTasks)
                subTasks = arrayListOf()
                if (view.parent != null) {
                    (view.parent as ViewGroup).removeView(view)
                }
            }
            if (dataTask.timer != "Указать >") {
                timer.text = dataTask.timer
                time = "01:00"
            } else {
                dialog.setOnShowListener { dialogInterface ->
                    (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = false
                }
            }
            if (dataTask.comment != "")
                comment.text = dataTask.comment
            if (dataTask.photo != "") {

                if (photo != null) {
                    (photo.drawable as? BitmapDrawable)!!.bitmap.recycle()
                }
                photo = view.findViewById<ImageButton>(R.id.imageViewPhoto)
                photo!!.visibility = ImageButton.VISIBLE
                photoText.visibility = TextView.VISIBLE

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                options.inSampleSize = 32
                val bitmap = BitmapFactory.decodeFile(dataTask.photo, options)
                photo.setImageBitmap(bitmap)
            }

            if (dataTask.video != "") {
                video.visibility = VideoView.VISIBLE
                videoText.visibility = TextView.VISIBLE
                video.setVideoURI(dataTask.video!!.toUri())
            }
            if (dataTask.audio != "" && dataTask.timeAudio == "") {

                playAudio.visibility = ImageButton.VISIBLE
                chronometer.visibility = Chronometer.VISIBLE
                audioText.visibility = TextView.VISIBLE

                playAudio.setOnClickListener {

                    playAudio(chronometer, dataTask.timeAudio!!, dataTask.audio!!)
                }
            }
        }
    }

    /*воспроизведение и остновка аудио*/
    fun playAudio(mChronometer: Chronometer, timeAudio: String, fileName: String) {

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

                mChronometer.setOnChronometerTickListener { chronometer ->
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

        if (timeAudio == "") {
            chronometer.base = SystemClock.elapsedRealtime()
        }

    }
}