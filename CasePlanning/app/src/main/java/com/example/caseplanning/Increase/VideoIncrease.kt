package com.example.caseplanning.Increase

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.R

class VideoIncrease : Fragment(){


    lateinit var pageViewModel : MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.video_zoom, container, false)

        val videoFile = view.findViewById<VideoView>(R.id.videoView)

        var uri : Uri? = null
        pageViewModel.getUri().observe(requireActivity(), Observer {
            uriTypeTask->
            uri = uriTypeTask.videoUri
        })

        val mediaController = MediaController(activity)
        videoFile.setMediaController(mediaController)
        mediaController.setAnchorView(videoFile)
        videoFile.setVideoURI(uri)
        videoFile.suspend()
        videoFile.start()
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }
}