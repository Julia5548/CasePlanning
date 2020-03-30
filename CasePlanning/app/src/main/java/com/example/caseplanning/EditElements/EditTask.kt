package com.example.caseplanning.EditElements

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.MainWindowCasePlanning
import com.example.caseplanning.R

class EditTask : Fragment() {

    private var editTextTaskName: EditText? = null
    private var textTask: String? = null
    val outState = Bundle()
    val listSubTasks = arrayListOf<String>()
    val listSubTasksView = arrayListOf<View>()
    private lateinit var pageViewModel : MyViewModel


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.edit_task, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarCreateTask)

        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Изменить"

        toolbar.setTitleTextColor(android.graphics.Color.GREEN)

        ButterKnife.bind(this, viewFragment)
        val linerLayoutSubTask = viewFragment.findViewById<RelativeLayout>(R.id.photo)
        linerLayoutSubTask.visibility = LinearLayout.GONE

        val relativeLayoutVideo = viewFragment.findViewById<RelativeLayout>(R.id.video)
        relativeLayoutVideo.visibility = LinearLayout.GONE

        return viewFragment
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var task : Task? = null

        pageViewModel.getTask().observe(requireActivity(), Observer<Task> {
            task = Task(name = it.name, nameSubTasks = it.nameSubTasks, shouldRepeat = it.shouldRepeat)
        })
        if (task != null) {
            val editTextTask = view.findViewById<EditText>(R.id.editTextTask)
            editTextTask.setText(task!!.name)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel  = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    /*получаем данные введенные пользователем в editText и передаем в активити WindowTask*/
    @OnClick(R.id.edit)
    fun onclickEdit() {

        val editTextTask = view!!.findViewById<EditText>(R.id.editTextTask)
        textTask = editTextTask.text.toString()
        for (position in 0 until listSubTasksView.size){
            listSubTasks.add(listSubTasksView[position]
                .findViewById<EditText>(R.id.editTextSubTasks)
                .text
                .toString())
            Log.d("Element", listSubTasks[position])
        }
        val task = Task(name = textTask!!,nameSubTasks = listSubTasks, shouldRepeat = true)
        pageViewModel.setTask(task)
        val intent = Intent(activity!!.applicationContext, MainWindowCasePlanning()::class.java)
        intent.putExtra("nameTask", textTask)
        startActivity(intent)
    }

    /*динамическое добавление подзадач*/
    @OnClick(R.id.addSubTasks)
    fun onClickAddSubTask() {

        val layoutInflater = layoutInflater

        val linerLayoutSubTask = view!!.findViewById<LinearLayout>(R.id.editSubTask)

        val viewFr = layoutInflater.inflate(R.layout.add_sub_tasks, null, false)

        val relativeLayoutSubTasks = viewFr.findViewById<RelativeLayout>(R.id.rel)


        relativeLayoutSubTasks.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (relativeLayoutSubTasks.parent != null) {
            (relativeLayoutSubTasks.parent as ViewGroup).removeView(relativeLayoutSubTasks)
        }
        listSubTasksView.add(viewFr)
        linerLayoutSubTask.addView(relativeLayoutSubTasks)

        val btnDeleted = viewFr.findViewById<ImageButton>(R.id.btnDeleted)
        btnDeleted.setOnClickListener { onClickDeletedSubTask(relativeLayoutSubTasks, viewFr) }

        val btnOkSubTasks = viewFr.findViewById<ImageButton>(R.id.btnOkCreate)
        btnOkSubTasks.setOnClickListener { onClickCreateSubTask(viewFr, btnDeleted, btnOkSubTasks) }
    }

    /*удаляет подзадачу*/
    fun onClickDeletedSubTask(relativeLayoutSubTasks: RelativeLayout, view : View) {

        // relativeLayoutSubTasks.visibility = RelativeLayout.GONE
        (view.parent as LinearLayout).removeView(view)
        listSubTasksView.remove(view)
        Log.d("Size", "${listSubTasksView.size}")
    }

    /*создает подзадачу*/
    fun onClickCreateSubTask(
        viewFr: View,
        btnDeletedOrEdit: ImageButton,
        btnOkSubTasks: ImageButton
    ) {

        val editTextSubTasks = viewFr.findViewById<EditText>(R.id.editTextSubTasks)
        editTextSubTasks.isEnabled = false
        btnDeletedOrEdit.setImageResource(R.drawable.ic_edit_black_24dp)
        btnDeletedOrEdit.setOnClickListener {
            onClickEditSubTask(
                editTextSubTasks,
                btnDeletedOrEdit,
                btnOkSubTasks,
                viewFr
            )
        }
        btnOkSubTasks.visibility = ImageButton.GONE
    }

    /*редактирование подзадачи*/
    private fun onClickEditSubTask(
        editTextSubTasks: EditText?,
        btnDeletedOrEdit: ImageButton,
        btnOkSubTasks: ImageButton,
        viewFr: View
    ) {

        val relativeLayoutSubTasks = viewFr.findViewById<RelativeLayout>(R.id.rel)

        editTextSubTasks!!.isEnabled = true
        editTextSubTasks.requestFocus()

        btnDeletedOrEdit.setImageResource(R.drawable.ic_delete_forever_black_24dp)
        btnOkSubTasks.visibility = ImageButton.VISIBLE

        btnDeletedOrEdit.setOnClickListener { onClickDeletedSubTask(relativeLayoutSubTasks, viewFr) }

        btnOkSubTasks.setOnClickListener {
            onClickCreateSubTask(
                viewFr,
                btnDeletedOrEdit,
                btnOkSubTasks
            )
        }
    }

}