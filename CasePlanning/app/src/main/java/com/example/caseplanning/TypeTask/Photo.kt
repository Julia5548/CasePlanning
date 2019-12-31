package com.example.caseplanning.TypeTask

import android.Manifest
import android.app.Activity.RESULT_OK
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.caseplanning.R

/*написать комменты, проблема с видом фотографии, нет потверждения на правильность фотографии, нажатие на фотографию, чтобы открылась полность вся, загрузка из галереи */
class Photo : Fragment(){

    val CAMERA_REQUEST = 1
    val PERMISSION_CODE = 1000
    lateinit var photo_image : ImageView
    lateinit var outputUriFile : Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view  = inflater.inflate(R.layout.photo, container, false)

        photo_image = view.findViewById<ImageButton>(R.id.photoImage)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context!!.checkSelfPermission(Manifest.permission.CAMERA)
                ==  PackageManager.PERMISSION_DENIED ||
                    context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                val permission : Array<String> = arrayOf(Manifest.permission.CAMERA,
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
    fun openCamera(){

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Task")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        outputUriFile = activity!!.contentResolver.insert(MediaStore.Images.
            Media.EXTERNAL_CONTENT_URI, values)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUriFile)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            //фотка сделана, извлекаем картинку
            photo_image.setImageURI(outputUriFile)
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