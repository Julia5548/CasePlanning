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
import com.example.caseplanning.CreateTaskWindow
import com.example.caseplanning.R
import com.example.caseplanning.WindowTask
import kotlinx.android.synthetic.main.task_window.*
import kotlinx.android.synthetic.main.task_window.view.*

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
   /* fun onClickAddTask(){



        /*к подзадачам*/
        addListTask.add(taskName.text.toString())

        val adapter = ArrayAdapter<String>(activity!!.applicationContext,
            android.R.layout.simple_list_item_1,
           addListTask)

      //  listTask.adapter = adapter
        taskName.setText("")
    }*/
}