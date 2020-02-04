package com.example.caseplanning.CreateTask

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
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.R

class CreateTaskWindow : Fragment() {

    private var editTextTaskName: EditText? = null
    private var textTask: String? = null
    val outState = Bundle()


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

        toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        ButterKnife.bind(this, viewFragment)



        return viewFragment
    }

    override fun onSaveInstanceState(outState: Bundle) {

        //inizializationEdit()
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
            //  inizializationEdit()

        } else {

            textTask = ""
            editTextTaskName = null

        }
        super.onCreate(savedInstanceState)

        Log.d("myLogs", "onCreate")
    }

    /*override fun onStop() {
        super.onStop()
        inizializationEdit()

        Log.d("myLogs", "onStop")
    }*/

/*AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
val textView = TextView(activity!!.applicationContext)
    textView.text = "Добавить подзадачу"
    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
    textView.textSize = 19F

    val ll = view!!.findViewById(R.id.SubTasks) as LinearLayout
    val lp = LinearLayout.
        LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    ll.addView(textView, lp)
 */
    /*добавление фото задачи*/
    /*  @OnClick(R.id.photo)
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
   */

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

        linerLayoutSubTask.addView(relativeLayoutSubTasks)

        val btnDeleted = viewFr.findViewById<ImageButton>(R.id.btnDeleted)
        btnDeleted.setOnClickListener { onClickDeletedSubTask(relativeLayoutSubTasks) }

        val btnOkSubTasks = viewFr.findViewById<ImageButton>(R.id.btnOkCreate)
        btnOkSubTasks.setOnClickListener { onClickCreateSubTask(viewFr, btnDeleted, btnOkSubTasks) }
    }

    /*удаляет подзадачу*/
    fun onClickDeletedSubTask(relativeLayoutSubTasks : RelativeLayout){

        relativeLayoutSubTasks.visibility = RelativeLayout.GONE
    }

    /*создает подзадачу*/
    fun onClickCreateSubTask(viewFr : View,
                             btnDeleted : ImageButton,
                             btnOkSubTasks : ImageButton )
    {

        val editTextSubTasks = viewFr.findViewById<EditText>(R.id.editTextSubTasks)
        editTextSubTasks.isEnabled = false
        btnDeleted.visibility = ImageButton.GONE
        btnOkSubTasks.visibility = ImageButton.GONE

    }

}