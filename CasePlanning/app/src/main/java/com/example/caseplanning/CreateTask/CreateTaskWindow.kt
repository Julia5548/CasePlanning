package com.example.caseplanning.CreateTask

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import butterknife.Optional
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.MainWindowCasePlanning
import com.example.caseplanning.R
import com.example.caseplanning.TypeTask.Photo
import com.example.caseplanning.TypeTask.Video
import kotlinx.android.synthetic.main.add_sub_tasks.view.*
import kotlinx.android.synthetic.main.type_task.*

class CreateTaskWindow : Fragment() {

    private var textTask: String? = null
    val listSubTasks = arrayListOf<String>()
    val listSubTasksView = arrayListOf<View>()
    private lateinit var pageViewModel: MyViewModel
    var textPeriod = ""

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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

        ButterKnife.bind(this, viewFragment)
        val linerLayoutSubTask = viewFragment.findViewById<RelativeLayout>(R.id.photo)
        linerLayoutSubTask.visibility = LinearLayout.GONE

        val relativeLayoutVideo = viewFragment.findViewById<RelativeLayout>(R.id.video)
        relativeLayoutVideo.visibility = LinearLayout.GONE

        return viewFragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var task: Task? = null

        pageViewModel.getTask().observe(requireActivity(), Observer<Task> { tasks ->
            task = Task(
                name = tasks.name,
                period = tasks.period
            )
        })

        val editTextTask = view.findViewById<EditText>(R.id.editTextTask)
        val textReplay = view.findViewById<TextView>(R.id.textChoose)

        if(task != null) {
            editTextTask.setText(task!!.name)

            val radioButtonMorning = view.findViewById<RadioButton>(R.id.radio_morning)
            val radioButtonDay = view.findViewById<RadioButton>(R.id.radio_day)
            val radioButtonEvening = view.findViewById<RadioButton>(R.id.radio_evening)
            val radioButtonOnceAnytime = view.findViewById<RadioButton>(R.id.radio_onceAnytime)

           when(task!!.period){
               radioButtonMorning.text.toString() ->
                   radioButtonMorning.isChecked = true
               radioButtonDay.text.toString() ->
                   radioButtonDay.isChecked = true
               radioButtonEvening.text.toString() ->
                   radioButtonEvening.isChecked = true
               radioButtonOnceAnytime.text.toString() ->
                    radioButtonOnceAnytime.isChecked = true
           }
        }


        if (arguments != null) {
             textReplay.text = arguments!!.getString("Replay")
        }
    }


    /*добавление фото задачи*/
    @OnClick(R.id.btnAddPhoto)
    fun onClickAddPhoto() {

        val linerLayoutSubTask = view!!.findViewById<RelativeLayout>(R.id.photo)
        linerLayoutSubTask.visibility = LinearLayout.VISIBLE

        val photo: Fragment = Photo()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.photo, photo)
        transaction.commit()
    }

    /*добавление видео задачи*/
    @OnClick(R.id.btnAddVideo)
    fun onClickAddVideo() {

        val relativeLayoutVideo = view!!.findViewById<RelativeLayout>(R.id.video)
        relativeLayoutVideo.visibility = LinearLayout.VISIBLE

        val video: Fragment = Video()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.video, video)
        transaction.commit()
    }
/*
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

      }*/

    @OnCheckedChanged
        (
        R.id.radio_morning,
        R.id.radio_day,
        R.id.radio_evening,
        R.id.radio_onceAnytime)
    fun onRadioButtonCheckChanged(button: CompoundButton, checked : Boolean){
        if(checked){
            textPeriod = button.text.toString()
        }
    }
    /*выбор повторения задачи*/
    @OnClick(R.id.textChoose)
    fun onClickChooseReplay() {

        val replay = Replay()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, replay)
        transaction.addToBackStack(null)
        transaction.commit()

        val task = saveDataTask()
        pageViewModel.setTask(task)
    }

/*      /*увелечение фотографии*/
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
*/

    /*динамическое добавление подзадач*/
    @OnClick(R.id.addSubTasks)
    fun onClickAddSubTask() {

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

        Log.d("Size", "${listSubTasksView.size}")
        val btnDeleted = viewFr.findViewById<ImageButton>(R.id.btnDeleted)
        btnDeleted.setOnClickListener { onClickDeletedSubTask(viewFr) }

        val btnOkSubTasks = viewFr.findViewById<ImageButton>(R.id.btnOkCreate)
        btnOkSubTasks.setOnClickListener { onClickCreateSubTask(viewFr, btnDeleted, btnOkSubTasks) }
    }

    /*удаляет подзадачу*/
    fun onClickDeletedSubTask(view: View) {

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

        editTextSubTasks!!.isEnabled = true
        editTextSubTasks.requestFocus()

        btnDeletedOrEdit.setImageResource(R.drawable.ic_delete_forever_black_24dp)
        btnOkSubTasks.visibility = ImageButton.VISIBLE

        btnDeletedOrEdit.setOnClickListener { onClickDeletedSubTask(viewFr) }

        btnOkSubTasks.setOnClickListener {
            onClickCreateSubTask(
                viewFr,
                btnDeletedOrEdit,
                btnOkSubTasks
            )
        }
    }

    /*получаем данные введенные пользователем в editText и передаем в активити WindowTask*/
    @OnClick(R.id.add)
    fun onclickAdd() {

        val task = saveDataTask()
        val dataBaseTask = DataBaseTask()
        dataBaseTask.createTask(task)

        val pageViewModel = ViewModelProviders.of(activity!!).get(MyViewModel::class.java)
        pageViewModel.setTask(task)

        val intent = Intent(activity!!.applicationContext, MainWindowCasePlanning()::class.java)
        startActivity(intent)
    }

    private fun saveDataTask(): Task {


        val editTextTask = view!!.findViewById<EditText>(R.id.editTextTask)
        textTask = editTextTask.text.toString()

        val textReplay = view!!.findViewById<TextView>(R.id.textChoose)
        val replay = textReplay.text.toString()

        for (position in 0 until listSubTasksView.size) {
            listSubTasks.add(
                listSubTasksView[position]
                    .findViewById<EditText>(R.id.editTextSubTasks)
                    .text
                    .toString()
            )
            Log.d("Element", listSubTasks[position])
        }
        return Task(name = textTask!!,
            period = textPeriod,
            replay = replay)
    }
}