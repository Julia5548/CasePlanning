package com.example.caseplanning.EditElements

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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnTouch
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.CreateTask.StorageFile
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.Increase.VideoIncrease
import com.example.caseplanning.R


class Video(val task: Task?, val tagger: String) : Fragment() {

    val CAMERA_REQUEST = 1
    val PERMISSION_CODE = 1001
    var outputUriFile: Uri? = null
    var tagList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            tagList = arguments!!.getStringArrayList("tagList")
            arguments == null
        }
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
            val file = outputUriFile.toString()
            val name = file.split("/")

            val storage = StorageFile(context = context!!, nameFile = name[6], path = file)
            storage.loadVideo()

            task!!.video = file
            if (tagger == "edit_task") {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, EditTask(task, tagList!!))
                    .commit()
            } else {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, CreateTaskWindow(task.day, task))
                    .commit()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        outputUriFile = null
    }
}