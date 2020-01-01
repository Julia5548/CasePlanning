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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R
import java.io.File
import java.lang.Exception

@Suppress("DEPRECATION")
class AudioTask : Fragment() {

    private var mediaRecorder : MediaRecorder? = null
    private var mediaPlayer : MediaPlayer? = null
    private lateinit var fileName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.audio, container, false)

        ButterKnife.bind(this, view)

        fileName = "${Environment.getExternalStorageDirectory()}/record.mp3"

        return view
    }

    /*запись аудио*/

    @OnClick(R.id.playRecord)
    fun onClickStartRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context!!.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED &&
                context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                val permission: Array<String> = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 0)
            } else {
                startRecording()
            }
            }else{

                startRecording()
            }
        }
    fun startRecording(){


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
            Log.d("Pfgbcm bltn", "OLLLLLLLLLO")
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun releaseRecorder() {
       if (mediaRecorder != null){
           mediaRecorder!!.release()
           mediaRecorder = null
       }
    }

    /*остановка записи задачи*/
    @OnClick(R.id.stopRecord)
    fun stopRecording(view: View){
        if (mediaRecorder != null){
            mediaRecorder!!.stop()
        }else{
            Log.d("XER", "BEEEEEEEEEEEEEEEEEEEEEEEEEE")
        }
    }

    @OnClick(R.id.stopPlay)
    fun stopPlayAudio(view: View){

        if(mediaPlayer != null)
            mediaPlayer!!.stop()

    }

    @OnClick(R.id.playAudio)
    fun playAudio(view: View){

        try{
            releasePlay()
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(fileName)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun releasePlay() {
        if (mediaPlayer != null){
            mediaPlayer!!.release()
            mediaPlayer = null
        }

    }

}