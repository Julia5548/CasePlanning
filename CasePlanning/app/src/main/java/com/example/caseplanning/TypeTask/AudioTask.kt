package com.example.caseplanning.TypeTask

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R
import java.io.File
import java.lang.Exception


class AudioTask : Fragment() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var fileName: String
    val PERMISSION_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.audio, container, false)

        ButterKnife.bind(this, view)

        fileName = "${Environment.getExternalStorageDirectory().absolutePath}/record.mp3"

        return view
    }

    /*запись и остановка аудио*/
    @OnClick(R.id.playRecordAndStopRecord)
    fun onClickStartRecording() {

        val playAndStopRecord = view!!.findViewById<ImageButton>(R.id.playRecordAndStopRecord)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context!!.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED &&
                context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                val permission: Array<String> = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(permission, 0)
            } else {
                if (playAndStopRecord.tag == null) {
                    playAndStopRecord.tag = 1
                    playAndStopRecord.setImageResource(R.drawable.ic_stop_black_24dp)
                    startRecording()
                } else if (playAndStopRecord.tag == 1) {
                    stopRecording()
                    playAndStopRecord.setImageResource(R.drawable.ic_mic_black_24dp)
                    playAndStopRecord.tag = null
                }
            }
        } else {

            if (playAndStopRecord.tag == null) {
                playAndStopRecord.tag = 1
                playAndStopRecord.setImageResource(R.drawable.ic_stop_black_24dp)
                startRecording()
            } else if (playAndStopRecord.tag == 1) {
                stopRecording()
                playAndStopRecord.setImageResource(R.drawable.ic_mic_black_24dp)
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
                playAndStopAudio.setImageResource(R.drawable.ic_stop_black_24dp)
                playAndStopAudio.tag = 1
            }else if (playAndStopAudio.tag == 1){
                stopPlayAudio()
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

}