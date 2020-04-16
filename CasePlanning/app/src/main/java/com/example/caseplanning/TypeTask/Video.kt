package com.example.caseplanning.TypeTask

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.R

class Video : Fragment(){

    val CAMERA_REQUEST = 1
    val PERMISSION_CODE = 1000
    lateinit var videoFie : VideoView
    lateinit var outputUriFile : Uri
    lateinit var pageViewModel:MyViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view  = inflater.inflate(R.layout.video, container, false)

        videoFie = view.findViewById<VideoView>(R.id.videoFile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context!!.checkSelfPermission(Manifest.permission.CAMERA)
                ==  PackageManager.PERMISSION_DENIED ||
                context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                val permission : Array<String> = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            }else{
                openCamera()
            }

        }else{
            openCamera()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }
    fun openCamera(){

        val values = ContentValues()
        values.put(MediaStore.Video.Media.TITLE, "New Task")
        values.put(MediaStore.Video.Media.DESCRIPTION, "From the camera")
        outputUriFile = activity!!.contentResolver.insert(
            MediaStore.Video.
                Media.EXTERNAL_CONTENT_URI, values)!!
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUriFile)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            //фотка сделана, извлекаем картинку
            videoFie.setVideoURI(outputUriFile)
            videoFie.seekTo(1)
            pageViewModel.uri.value = UriTypeTask(videoUri = outputUriFile)
           // videoFie.suspend()
            //videoFie.start()
        }else{
            Log.d("Popka", "RUNNNN")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else{
                    Toast.makeText(activity!!.applicationContext, "В доступе было отказано",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}