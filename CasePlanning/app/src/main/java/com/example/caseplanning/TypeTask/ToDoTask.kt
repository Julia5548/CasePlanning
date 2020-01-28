package com.example.caseplanning.TypeTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.caseplanning.R

class  ToDoTask : Fragment() {

    /*подзадачам*/
    val addListTask = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.to_do, container, false)
            return view
    }
}