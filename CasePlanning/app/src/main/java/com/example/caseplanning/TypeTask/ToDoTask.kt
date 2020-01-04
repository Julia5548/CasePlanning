package com.example.caseplanning.TypeTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R
import kotlinx.android.synthetic.main.task_window.view.*

class ToDoTask : Fragment() {

    val addListTask = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.to_do, container, false)

        ButterKnife.bind(this,view)

            return view
    }
    @OnClick(R.id.addTaskToDo)
    fun onClickAddTask(){
        val taskName = view!!.findViewById<EditText>(R.id.taskText)
        val listTask = view!!.findViewById<ListView>(R.id.listTask)

        addListTask.add(taskName.text.toString())

        val adapter = ArrayAdapter<String>(activity!!.applicationContext,
            android.R.layout.simple_list_item_1,
           addListTask)

        listTask.adapter = adapter
        taskName.setText("")
    }
}