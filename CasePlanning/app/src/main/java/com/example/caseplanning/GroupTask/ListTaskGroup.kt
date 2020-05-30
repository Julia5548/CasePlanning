package com.example.caseplanning.GroupTask

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerView
import com.example.caseplanning.adapter.AdapterRecyclerViewFolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListTaskGroup(nameFolder: String, listTasks: ArrayList<Task>?) : Fragment() {

    val mNameFolder = nameFolder
    val mListTasks = listTasks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.folder_tasks, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?

        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = mNameFolder

        actionBar.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        ButterKnife.bind(this, view)

        listTaskGroup(view)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_task, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                val groupTask: Fragment = GroupTask()
                fragmentManager!!.beginTransaction().replace(R.id.linerLayout, groupTask).commit()
            }
            R.id.add_task_group -> {
                val database = DataBaseTask()
                val view = layoutInflater.inflate(R.layout.list_tasks, null)
                val taskList = view.findViewById<RecyclerView>(R.id.list_task_item)
                val task_item = arrayListOf<String>()
                val disposable = database
                    .retrieveData()
                    .subscribe { tasks ->
                        for (task in tasks) {
                            task_item.add(task.name!!)
                        }
                        taskList.layoutManager = LinearLayoutManager(context!!)
                        taskList.adapter = AdapterRecyclerView(context!!, task_item)

                        MaterialAlertDialogBuilder(context)
                            .setTitle("Список задач")
                            .setView(view)
                            .setPositiveButton("Ок") { dialog, id ->
                                dialog.dismiss()
                            }
                            .setNegativeButton("Отмена") { dialogInterface, id ->
                                dialogInterface.dismiss()
                            }
                            .show()
                    }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun listTaskGroup(view: View) {

        val listTask = view.findViewById<RecyclerView>(R.id.list_task_group)
        val nameTask = arrayListOf<String>()
        if (mListTasks != null) {
            for (task in mListTasks)
                nameTask.add(task.name!!)
        }

        listTask.layoutManager = LinearLayoutManager(context)
        listTask.adapter = AdapterRecyclerViewFolder(context!!, nameTask)
    }
}