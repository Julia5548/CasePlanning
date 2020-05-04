package com.example.caseplanning.TypeTask

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnTouch
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.Increase.VideoIncrease
import com.example.caseplanning.R

class Video : Fragment() {

    val CAMERA_REQUEST = 1
    val PERMISSION_CODE = 1001
    var videoFie: VideoView? = null
    var outputUriFile: Uri? = null
    var pageViewModel: MyViewModel? = null
    var photoUri: String? = ""
    var audioFile: String? = ""
    var timeAudio: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.video, container, false)

        videoFie = view.findViewById<VideoView>(R.id.videoFile)

        ButterKnife.bind(this, view)

        pageViewModel!!.uri.observe(requireActivity(), Observer { uri ->
            if (uri != null) {
                outputUriFile = uri.videoUri?.toUri()
                photoUri = uri.photoUri
                audioFile = uri.audioUri
                timeAudio = uri.timeAudio
            }
        })


        if (outputUriFile == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context!!.checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission: Array<String> = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openCamera()
                }

            } else {
                openCamera()
            }
        } else {
            videoFie!!.setVideoURI(outputUriFile)
            videoFie!!.seekTo(1)
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Video.Media.TITLE, "New Task")
        values.put(MediaStore.Video.Media.DESCRIPTION, "From the camera")
        outputUriFile = activity!!.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values
        )!!
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUriFile)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //фотка сделана, извлекаем картинку
            videoFie!!.setVideoURI(outputUriFile)
            videoFie!!.seekTo(1)
            pageViewModel!!.uri.value =
                UriTypeTask(videoUri = outputUriFile.toString(), photoUri = photoUri, audioUri = audioFile, timeAudio = timeAudio)
        } else {
            Log.d("Ошибка", "Не удалось сохранить видео")
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
                    openCamera()
                } else {
                    Toast.makeText(
                        activity!!.applicationContext, "В доступе было отказано",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @OnTouch(R.id.videoFile)
    fun videoZoom() {
        val videoIncrease: Fragment = VideoIncrease()
        val transaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, videoIncrease)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        pageViewModel = null
        videoFie?.setVideoURI(null)
        outputUriFile = null
        audioFile = null
        photoUri = null
        timeAudio = null

    }
}