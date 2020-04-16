package com.example.caseplanning.Increase

import android.opengl.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.R
import com.github.chrisbanes.photoview.PhotoView

class PhotoIncrease : Fragment() {

    lateinit var pageViewModel : MyViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_zoom, container, false)

        ButterKnife.bind(this, view)


        val photoView:PhotoView = view.findViewById(R.id.imageZoom)
        val activity = activity as AppCompatActivity?
        activity!!.supportActionBar!!.hide()

        pageViewModel.uri.observe(requireActivity(), Observer {
            uri->
            photoView.setImageURI(uri.photoUri)
        })


        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

    }

}