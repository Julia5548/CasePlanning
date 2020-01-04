package com.example.caseplanning.TypeTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R

class TextTask : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.text_task, container, false)

        ButterKnife.bind(this,view)

        return view
    }
    @OnClick(R.id.addTask)
    fun onClickAddTask(){
        val editTaskText = view!!.findViewById<EditText>(R.id.editTaskText)
        val taskText = view!!.findViewById<TextView>(R.id.taskText)

        taskText.text = editTaskText.text.toString()
        editTaskText.setText("")
    }
}