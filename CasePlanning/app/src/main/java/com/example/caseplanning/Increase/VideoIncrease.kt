package com.example.caseplanning.Increase

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.R

class VideoIncrease : Fragment() {

    var pageViewModel: MyViewModel? = null
    lateinit var videoFile: VideoView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.video_zoom, container, false)

        videoFile = view.findViewById<VideoView>(R.id.videoView)


        ButterKnife.bind(this, view)

        var videoUri: Uri? = null

        pageViewModel!!.uri.observe(requireActivity(), Observer { uriTypeTask ->
            if (uriTypeTask != null)
                videoUri = uriTypeTask.videoUri
        })

        var mediaController: MediaController? = null
        if (videoUri != null) {


            mediaController = object : MediaController(context) {

                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        val createTask: Fragment = CreateTaskWindow()
                        val transaction = fragmentManager!!.beginTransaction()

                        transaction.replace(R.id.linerLayout, createTask)
                        transaction.commit()

                        return true
                    }
                    return super.dispatchKeyEvent(event)
                }
            }
            videoFile.setMediaController(mediaController)
            videoFile.setVideoURI(videoUri)
            mediaController.setAnchorView(videoFile)
            videoFile.requestFocus()
            videoFile.start()
        }

        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoFile.stopPlayback()
        videoFile.setVideoURI(null)
        pageViewModel = null
    }
}