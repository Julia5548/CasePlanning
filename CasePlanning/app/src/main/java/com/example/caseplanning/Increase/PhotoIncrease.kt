package com.example.caseplanning.Increase

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.example.caseplanning.R
import com.example.caseplanning.TypeTask.Photo

class PhotoIncrease : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_zoom, container, false)

        ButterKnife.bind(this, view)

        val photo = Photo()
        val imageView = view.findViewById<ImageView>(R.id.imageZoom)
        imageView.setImageURI(photo.outputUriFile)


        return view
    }
}