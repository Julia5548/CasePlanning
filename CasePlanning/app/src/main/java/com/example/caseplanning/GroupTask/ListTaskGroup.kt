package com.example.caseplanning.GroupTask

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerViewFolder
import com.example.caseplanning.adapter.SwipeToDeleteCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ListTaskGroup(nameFolder: String, listTasks: ArrayList<Task>?, key: String) : Fragment() {

    val mNameFolder = nameFolder
    val mListTasks = listTasks
    val mKey = key
    var mAdapterFolderTask: AdapterRecyclerViewFolder? = null

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
                var task_item: Array<String?>
                val tasks_list = arrayListOf<Task>()
                val checkedTask = arrayListOf<Task>()
                var position = 0
                val disposable = database
                    .retrieveData(FirebaseAuth.getInstance().currentUser!!.uid)
                    .subscribe { tasks ->
                        task_item = arrayOfNulls(tasks.size)
                        for (task in tasks) {
                            task_item[position] = task.name!!
                            tasks_list.add(task)
                            position++
                        }
                        val checkedItems = BooleanArray(task_item.size)
                        for (positionChecked in task_item.indices) {
                            checkedItems[positionChecked] = false
                        }
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Список задач")
                            .setMultiChoiceItems(
                                task_item,
                                checkedItems
                            ) { dialog, which, isChecked ->
                                checkedItems[which] = isChecked
                            }
                            .setPositiveButton("Ок") { dialog, which ->
                                if (mListTasks?.size != 0) {
                                    for (positionList in mListTasks!!.indices)
                                        checkedTask.add(mListTasks[positionList])
                                }
                                for (idItems in checkedItems.indices) {
                                    if (checkedItems[idItems]) {
                                        if (tasks_list[idItems].name == task_item[idItems])
                                            checkedTask.add(tasks_list[idItems])
                                    }
                                }
                                val folder = Folder(name = mNameFolder, tasks = checkedTask)
                                database.updateDataFolder(folder = folder, key = mKey)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Отмена") { dialogInterface, which ->
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
        mAdapterFolderTask = AdapterRecyclerViewFolder(context!!, nameTask)
        listTask.adapter = mAdapterFolderTask

        enableSwipeToDeleteAndUndo(listTask, view)
    }

    private fun enableSwipeToDeleteAndUndo(listFolder: RecyclerView, view: View) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.linerLayout_listTaskFolder)
        val dataBaseTask = DataBaseTask()
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(context!!) {
                override fun onSwiped(
                    @NonNull viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val deleted_item = mListTasks!![position]
                    mListTasks.removeAt(position)
                    val folder = Folder(name = mNameFolder, tasks = mListTasks)
                    dataBaseTask.updateDataFolder(folder, mKey)
                    mAdapterFolderTask!!.notifyDataSetChanged()
                    val snackbar = Snackbar.make(
                        linearLayout,
                        "Задача была удалена из папки",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("Отменить") { _ ->
                        mListTasks.add(deleted_item)
                         val folderItem =
                            Folder(id = "", name = mNameFolder, tasks = mListTasks)
                        dataBaseTask.updateDataFolder(folder, mKey)
                        mAdapterFolderTask!!.notifyDataSetChanged()
                        listFolder.scrollToPosition(position)
                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(listFolder)
    }
}