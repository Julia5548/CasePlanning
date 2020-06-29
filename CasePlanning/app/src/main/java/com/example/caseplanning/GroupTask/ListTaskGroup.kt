package com.example.caseplanning.GroupTask

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

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
                val view = layoutInflater.inflate(R.layout.create_folder, null)
                val outlinedTextField = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
                outlinedTextField.hint = "Наименование задачи"

                MaterialAlertDialogBuilder(context)
                    .setTitle("Добавить задачу")
                    .setView(view)
                    .setPositiveButton("Добавить") { dialogInterface, id ->
                        val nameNewTask = outlinedTextField.editText!!.text.toString()
                        val task = Task(name = nameNewTask, checked = false)
                        val calendar= Calendar.getInstance()
                        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val formatedDate = format.format(calendar.time)
                        mFolder!!.tasks!!.add(task)
                        mFolder!!.date = formatedDate
                        database.updateDataFolder(folder = mFolder!!, key = mFolder!!.id)

                        Toast.makeText(
                            context,
                            "Задача $nameNewTask успешно создана",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .setNegativeButton("Отменить", null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun listTaskGroup(view: View) {

        val listTask = view.findViewById<RecyclerView>(R.id.list_task_group)
        val taskList = arrayListOf<Task>()

        if (mFolder!!.tasks != null) {
            for (task in mFolder!!.tasks!!) {
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
                        progress = progress.toString(),
                        date = mFolder!!.date
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