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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.FragmentDialog

class VideoIncrease(val task: Task?, val tagger : String) : Fragment() {

    private var tagList : ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.video_zoom, container, false)

        val videoFile = view.findViewById<VideoView>(R.id.videoView)

        ButterKnife.bind(this, view)

        val videoUri: Uri? = if(task!!.video != null && task.video !=""){
            task.video!!.toUri()
        }else{
            null
        }

        if(arguments != null){
            tagList = arguments!!.getStringArrayList("tagList")
            arguments == null
        }
        var mediaController: MediaController? = null
        if (videoUri != null) {
            mediaController = object : MediaController(context) {

                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        if(tagger == "edit_task") {
                            fragmentManager!!.beginTransaction().remove(this@VideoIncrease)
                                .replace(R.id.linerLayout, EditTask(task, tagList!!)).commit()
                        }else if (tagger == "create_task"){
                            fragmentManager!!.beginTransaction().remove(this@VideoIncrease)
                                .replace(R.id.linerLayout, CreateTaskWindow(task.day, task)).commit()
                        }else{
                            fragmentManager!!.beginTransaction().remove(this@VideoIncrease)
                                .replace(R.id.linerLayout, FragmentDialog(task)).commit()
                        }
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

    override fun onDestroy() {
        super.onDestroy()
    }
}