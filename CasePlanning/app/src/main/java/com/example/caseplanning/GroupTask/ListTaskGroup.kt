package com.example.caseplanning.GroupTask

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerViewTaskFolder
import com.example.caseplanning.adapter.SwipeToDeleteCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.w3c.dom.Text

class ListTaskGroup(
    folder: Folder
) : Fragment() {

    var mFolder: Folder? = folder
    var mAdapterFolderTask: AdapterRecyclerViewTaskFolder? = null

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
        actionBar!!.title = mFolder!!.name

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
                val database = DataBase()
                var task_item: Array<String?>
                val tasks_list = arrayListOf<Task>()
                val checkedTask = arrayListOf<Task>()
                var position = 0
                val disposable = database
                    .retrieveData(FirebaseAuth.getInstance().currentUser!!.uid)
                    .subscribe { tasks ->
                        task_item = if (mFolder!!.tasks != null) {
                            arrayOfNulls(tasks.size - mFolder!!.tasks!!.size)
                        } else {
                            arrayOfNulls(tasks.size)
                        }
                        for (task in tasks) {
                            if (mFolder!!.tasks != null && !mFolder!!.tasks!!.contains(task)) {
                                task_item[position] = task.name!!
                                tasks_list.add(task)
                                position++
                            }
                        }
                        val checkedItems = BooleanArray(task_item.size)
                        for (positionChecked in task_item.indices)
                            checkedItems[positionChecked] = false

                        MaterialAlertDialogBuilder(context)
                            .setTitle("Список задач")
                            .setMultiChoiceItems(
                                task_item,
                                checkedItems
                            ) { dialog, which, isChecked ->
                                checkedItems[which] = isChecked
                            }
                            .setPositiveButton("Ок") { dialog, which ->
                                if (mFolder!!.tasks?.size != 0) {
                                    for (positionList in mFolder!!.tasks!!.indices)
                                        checkedTask.add(mFolder!!.tasks!![positionList])
                                }
                                for (idItems in checkedItems.indices) {
                                    if (checkedItems[idItems]) {
                                        if (tasks_list[idItems].name == task_item[idItems])
                                            checkedTask.add(tasks_list[idItems])
                                    }
                                }
                                var checked_count = 0.0
                                for (folder_task in checkedTask) {
                                    if (folder_task.checked!!) {
                                        checked_count++
                                    }
                                }
                                //Расчет для прогресса
                                val progress: Double = (checked_count / checkedTask.size) * 100
                                val folder = Folder(
                                    name = mFolder!!.name,
                                    tasks = checkedTask,
                                    progress = progress.toString()
                                )
                                database.updateDataFolder(folder = folder, key = mFolder!!.id)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Отмена") { dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            .show()
                    }
                if (disposable != null && disposable.isDisposed)
                    disposable.dispose()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun listTaskGroup(view: View) {

        val listTask = view.findViewById<RecyclerView>(R.id.list_task_group)
        val taskList = arrayListOf<Task>()

        if (mFolder!!.tasks != null) {
            for (task in mFolder!!.tasks!!) {
                if (task.checked == false)
                    taskList.add(task)
            }
            for (task in mFolder!!.tasks!!) {
                if (task.checked!!)
                    taskList.add(task)
            }

            listTask.layoutManager = LinearLayoutManager(context)
            mAdapterFolderTask =
                AdapterRecyclerViewTaskFolder(context!!, taskList, mFolder)
            listTask.adapter = mAdapterFolderTask

            enableSwipeToDeleteAndUndo(listTask, view)
        }
    }

    private fun enableSwipeToDeleteAndUndo(listFolder: RecyclerView, view: View) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.linerLayout_listTaskFolder)
        val dataBaseTask = DataBase()
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(context!!) {
                override fun onSwiped(
                    @NonNull viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val deleted_item = mAdapterFolderTask!!.mData[position]
                    if (mFolder!!.tasks!!.contains(deleted_item)) {
                        mFolder!!.tasks!!.remove(deleted_item)
                    }
                    mAdapterFolderTask!!.mData.removeAt(position)

                    var checked_count = 0.0
                    for (folder_task in mFolder!!.tasks!!) {
                        if (folder_task.checked!!) {
                            checked_count++
                        }
                    }
                    //Расчет для прогресса

                    var progress = 0.0
                    if (mFolder!!.tasks!!.size != 0)
                        progress = (checked_count / mFolder!!.tasks!!.size) * 100

                    val folder = Folder(
                        name = mFolder!!.name,
                        tasks = mFolder!!.tasks,
                        progress = progress.toString()
                    )
                    dataBaseTask.updateDataFolder(folder, mFolder!!.id)
                    mAdapterFolderTask!!.notifyDataSetChanged()
                    val snackbar = Snackbar.make(
                        linearLayout,
                        "Задача была удалена из папки",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("Отменить") { _ ->
                        mFolder!!.tasks!!.add(deleted_item)

                        //Расчет для прогресса
                        progress = (checked_count / mFolder!!.tasks!!.size) * 100
                        folder.progress = progress.toString()

                        dataBaseTask.updateDataFolder(folder, mFolder!!.id)
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

    override fun onDestroy() {
        super.onDestroy()
        mAdapterFolderTask = null
        mFolder = null
    }
}