package com.example.caseplanning.Photo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.caseplanning.R
import java.io.File

class Photo : Fragment(){

    val CAMERA_REQUEST = 1
    lateinit var photo_image : ImageButton
    lateinit var outputUriFile : Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view  = inflater.inflate(R.layout.photo, container, false)

        photo_image = view.findViewById<ImageButton>(R.id.photoImage)
        creationIntentPhoto()

        return view
    }
    fun creationIntentPhoto(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
        saveFullImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            //фотка сделана, извлекаем картинку
            val thumbnailBitmap = data!!.getExtras()!!.get("data") as Bitmap
            photo_image.setImageBitmap(thumbnailBitmap)
        }else{
            Log.d("Popka", "RUNNNN")
        }
    }
    private fun saveFullImage(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(Environment.getExternalStorageDirectory(), "test.jpg")
        outputUriFile = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUriFile)
        startActivityForResult(intent, CAMERA_REQUEST)

    }
}