package com.example.caseplanning.TypeTask

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.CreateTask.StorageFile
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.Increase.PhotoIncrease
import com.example.caseplanning.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.E

class Photo(val task: Task?, val tagger: String) : Fragment() {

    val CAMERA_REQUEST = 1001
    val PERMISSION_CODE = 1000
    private var mCurrentFile: String? = null
    private var mPhotoFile: File? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    /*открываем камеру*/
    fun openCamera() {
        mPhotoFile = createPhotoFile()
        photoUri = createPhotoUri(mPhotoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri!!)
            startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    private fun createPhotoUri(file: File?): Uri {

        return FileProvider.getUriForFile(
            context!!,
            "${context!!.applicationContext.packageName}.provider",
            file!!
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val name = mCurrentFile!!.split("/")
            val storage =
                StorageFile(context = context!!, nameFile = name[9], path = mCurrentFile!!)
            storage.loadImages()
            task!!.photo = mCurrentFile!!
            if (tagger == "edit_task") {
                fragmentManager!!.beginTransaction().replace(R.id.linerLayout, EditTask(task))
                    .commit()
            } else {
                fragmentManager!!.beginTransaction().replace(R.id.linerLayout, CreateTaskWindow(task.day, task))
                    .commit()
            }
            loadPhotoinGallery()
        } else {
            Log.d("Ошибка", "Не получилось сохранить фотографию")
        }
    }

    private fun loadPhotoinGallery() {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

        intent.data = photoUri!!
        context!!.sendBroadcast(intent)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createPhotoFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "DSC_${timeStamp}_"
        val storage = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val imageFile = File.createTempFile(
            imageFileName,
            ".JPG",
            storage
        )
        mCurrentFile = imageFile.absolutePath
        return imageFile
    }

    /*разрешение доступа к камере*/
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
                        context, "В доступе было отказано",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCurrentFile = null
        mPhotoFile = null
        photoUri = null
    }
}