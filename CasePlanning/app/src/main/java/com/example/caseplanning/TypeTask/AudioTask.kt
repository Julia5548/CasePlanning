package com.example.caseplanning.TypeTask

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.R
import java.io.File
import java.lang.Exception


class AudioTask : Fragment() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var fileName: String
    val PERMISSION_CODE = 1000
    var videoUri: Uri? = null
    var photoUri: String? = null
     var chronometer: Chronometer? = null
    var timeAudio: String? = null
    var audioFile : String? = null

    private var pageViewModel: MyViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.audio, container, false)

        ButterKnife.bind(this, view)

        chronometer = view!!.findViewById<Chronometer>(R.id.timerText)


        fileName =
            "${Environment.getExternalStorageDirectory().absolutePath}/${System.currentTimeMillis()}record.mp3"

        pageViewModel!!.uri.observe(requireActivity(), Observer { uri ->
            if (uri != null) {
                videoUri = uri.videoUri
                photoUri = uri.photoUri
                timeAudio = uri.timeAudio
                audioFile = uri.audioUri!!
            }
        })

        if(audioFile != null){
            val playAndStopRecord = view.findViewById<ImageButton>(R.id.playRecordAndStopRecord)

            val playAndStopAudio = view.findViewById<ImageButton>(R.id.playAndStopAudio)
            playAndStopRecord.visibility = ImageButton.GONE
            playAndStopAudio.visibility = ImageButton.VISIBLE

            fileName = audioFile!!
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

    }

    /*запись и остановка аудио*/
    @OnClick(R.id.playRecordAndStopRecord)
    fun onClickStartRecording() {

        val playAndStopRecord = view!!.findViewById<ImageButton>(R.id.playRecordAndStopRecord)

        val playAndStopAudio = view!!.findViewById<ImageButton>(R.id.playAndStopAudio)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                val permission: Array<String> = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(requireActivity(), permission, 0)
            } else {
                if (playAndStopRecord.tag == null) {
                    playAndStopRecord.tag = 1
                    playAndStopRecord.setImageResource(R.drawable.ic_stop_black_24dp)
                    startRecording()
                } else if (playAndStopRecord.tag == 1) {
                    stopRecording()
                    playAndStopRecord.visibility = ImageButton.GONE
                    playAndStopAudio.visibility = ImageButton.VISIBLE
                }
            }
        } else {

            if (playAndStopRecord.tag == null) {
                playAndStopRecord.tag = 1
                playAndStopRecord.setImageResource(R.drawable.ic_stop_black_24dp)
                startRecording()
            } else if (playAndStopRecord.tag == 1) {
                stopRecording()
                playAndStopRecord.visibility = ImageButton.GONE
                playAndStopAudio.visibility = ImageButton.VISIBLE
            }
        }

    }

    /*запись аудио записи*/
    fun startRecording() {

        try {
            releaseRecorder()
            val outputFile = File(fileName)
            if (outputFile.exists())
                outputFile.delete()
            mediaRecorder = MediaRecorder()
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder!!.setOutputFile(fileName)
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            chronometer!!.base = SystemClock.elapsedRealtime()
            chronometer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder!!.release()
            mediaRecorder = null
        }
    }

    /*остановка записи аудио*/
    fun stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder!!.stop()
            chronometer!!.stop()
            timeAudio = chronometer!!.text.toString()
            chronometer!!.base = SystemClock.elapsedRealtime()
            pageViewModel!!.uri.value =
                UriTypeTask(audioUri = fileName, photoUri = photoUri, videoUri = videoUri, timeAudio = timeAudio)
        } else {
            Log.d("Tag", "Audio stop is not possible")
        }
    }

    /*воспроизведение и остновка аудио*/
    @OnClick(R.id.playAndStopAudio)
    fun playAudio() {

        val playAndStopAudio = view!!.findViewById<ImageButton>(R.id.playAndStopAudio)
        try {

            if (playAndStopAudio.tag == null) {
                releasePlay()
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(fileName)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                chronometer!!.base = SystemClock.elapsedRealtime()
                chronometer!!.start()
                playAndStopAudio.setImageResource(R.drawable.ic_stop_black_24dp)
                playAndStopAudio.tag = 1

                chronometer!!.setOnChronometerTickListener {
                    chronometer ->
                    if (timeAudio != null) {
                        if (chronometer.text.toString() == timeAudio) {
                            chronometer.stop()
                            timeAudio = null
                            stopPlayAudio()
                            playAndStopAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                            playAndStopAudio.tag = null
                        }
                    }
                }

            } else if (playAndStopAudio.tag == 1) {
                stopPlayAudio()
                chronometer!!.stop()
                chronometer!!.base = SystemClock.elapsedRealtime()
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
    fun stopPlayAudio() {

        if (mediaPlayer != null)
            mediaPlayer!!.stop()

        if(timeAudio == null){
            chronometer!!.base = SystemClock.elapsedRealtime()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(
                        activity!!.applicationContext, "В доступе было отказано",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaRecorder = null
        mediaPlayer = null
        videoUri = null
        photoUri = null
        pageViewModel = null
        timeAudio = null
        chronometer = null


    }

}