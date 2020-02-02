package com.example.caseplanning.CreateTask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.Increase.PhotoIncrease
import com.example.caseplanning.MainWindowCasePlanning
import com.example.caseplanning.R
import com.example.caseplanning.TypeTask.*
import kotlinx.android.synthetic.main.task_window.*

class CreateTaskWindow : Fragment() {

    private var editTextTaskName: EditText? = null
    private var textTask: String? = null
    val outState = Bundle()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.task_window, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarCreateTask)

        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Создать"

        toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        ButterKnife.bind(this, viewFragment)



        return viewFragment
    }

    override fun onSaveInstanceState(outState: Bundle) {

        inizializationEdit()
        outState.run {
            putString("textTask", editTextTaskName!!.text.toString())
        }
        super.onSaveInstanceState(outState)

        Log.d("myLogs", "onSaveInstanceState")
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (outState.isEmpty) {
            savedInstanceState == null
        }

        if (savedInstanceState != null) {

            textTask = savedInstanceState.getString("nameTask", "")
            inizializationEdit()

        } else {

            textTask = ""
            editTextTaskName = null

        }
        super.onCreate(savedInstanceState)

        Log.d("myLogs", "onCreate")
    }

    override fun onStop() {
        super.onStop()
        inizializationEdit()

        Log.d("myLogs", "onStop")
    }


    /*добавление фото задачи*/
    @OnClick(R.id.photo)
    fun onClickAddPhoto() {

        val photo: Fragment = Photo()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, photo)
        transaction.commit()

    }

    /*добавление видео задачи*/
    @OnClick(R.id.video)
    fun onClickAddVideo() {

        val video: Fragment = Video()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, video)
        transaction.commit()

    }

    /*добавление аудио задачи*/
    @OnClick(R.id.audio)
    fun onClickAddAudio() {

        val audio = AudioTask()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, audio)
        transaction.commit()

    }

    /*добавление задачи в виде списка*/
    @OnClick(R.id.to_do)
    fun onClickAddToDoTask() {

        val toDoTask = ToDoTask()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, toDoTask)
        transaction.commit()

    }

    /*добавление задачи в виде текста*/
    @OnClick(R.id.text)
    fun onClickAddTextTask() {

        val textTask = TextTask()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, textTask)
        transaction.commit()

    }

    /*выбор повторения задачи*/
    @OnClick(R.id.textChoose)
    fun onClickChooseReplay() {


        onSaveInstanceState(outState)

        val replay = Replay()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, replay)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /*увелечение фотографии*/
    fun photoZoom() {
        val photoIncrease: Fragment = PhotoIncrease()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.relativeLayout, photoIncrease)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /*Добавить подзадачу*/
    @OnClick(R.id.btnAddSubTask)
    fun onClickAddSubTask() {

        val listViewSubTask = view!!.findViewById<ListView>(R.id.listSubTask)
        val subTask = SubTasks()
        val listSubTask = subTask.createSubTask(listViewSubTask)

        val adapter = ArrayAdapter<String>(
            activity!!.applicationContext,
            android.R.layout.simple_list_item_1,
            listSubTask
        )
        listViewSubTask.adapter = adapter
    }

    /*получаем данные введенные пользователем в editText и передаем в активити WindowTask*/
    @OnClick(R.id.add)
    fun onclickAdd() {

        inizializationEdit()

        val dataBaseTask = DataBaseTask()
        dataBaseTask.createTask(textTask!!)

        if (arguments != null) {
            val value = arguments!!.getString("Period")
        }

        val intent = Intent(activity!!.applicationContext, MainWindowCasePlanning()::class.java)
        intent.putExtra("nameTask", textTask)
        startActivity(intent)
    }


    private fun inizializationEdit() {
        editTextTaskName = activity!!.findViewById<EditText>(R.id.taskText)

    }
}