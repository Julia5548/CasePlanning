package com.example.caseplanning.Increase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.KeyEventDispatcher
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.CreateTask.StorageFile
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.R
import com.github.chrisbanes.photoview.PhotoView
import java.io.ByteArrayOutputStream

class PhotoIncrease(val task : Task?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_zoom, container, false)
        val photoView: PhotoView = view.findViewById(R.id.imageZoom)
        val activity = activity as AppCompatActivity?
        activity!!.supportActionBar!!.hide()

        val photoPath = task!!.photo!!
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = 5

        val bitmap = BitmapFactory.decodeFile(photoPath, options)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        photoView.setImageBitmap(bitmap)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy")
    }

}