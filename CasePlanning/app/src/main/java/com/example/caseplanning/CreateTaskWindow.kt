package com.example.caseplanning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.caseplanning.TypeTask.*
import kotlinx.android.synthetic.main.task_window.view.*
import kotlinx.android.synthetic.main.to_do.*

class CreateTaskWindow() : Fragment(){

    private lateinit var listView:ListView
    constructor(listView: ListView) : this() {
        this.listView = listView
    }
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
        val dataBaseTask = DataBaseTask(listView)
        dataBaseTask.writeDataBase()

        return viewFragment
    }
    /*добавление фото задачи*/
    @OnClick(R.id.photo)
    fun onClickAddPhoto(){

        val photo: Fragment = Photo()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, photo)
        transaction.commit()

    }
    /*добавление видео задачи*/
    @OnClick(R.id.video)
    fun onClickAddVideo(){

        val video: Fragment = Video()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, video)
        transaction.commit()

    }
    /*добавление аудио задачи*/
    @OnClick(R.id.audio)
    fun onClickAddAudio(){

        val audio = AudioTask()
        val transaction:FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, audio)
        transaction.commit()

    }

    /*добавление задачи в виде списка*/
    @OnClick(R.id.to_do)
    fun onClickAddToDoTask(){

        val toDoTask = ToDoTask()
        val transaction:FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, toDoTask)
        transaction.commit()


    }

    /*добавление задачи в виде текста*/
    @OnClick(R.id.text)
    fun onClickAddTextTask(){

        val textTask = TextTask()
        val transaction:FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.typeTask, textTask)
        transaction.commit()

    }
    /*получаем данные введенные пользователем в editText и передаем в активити WindowTask*/
    @OnClick(R.id.add)
    fun onclickAdd(){
        val editTextTaskName = view!!.findViewById<EditText>(R.id.taskText)
        val textTask = editTextTaskName.text.toString()

        val dataBaseTask = DataBaseTask()
        dataBaseTask.onClickAddTask(textTask)

        val intent = Intent(activity!!.applicationContext, MainWindowCasePlanning()::class.java )
        intent.putExtra("nameTask", textTask)
        startActivity(intent)
    }

}