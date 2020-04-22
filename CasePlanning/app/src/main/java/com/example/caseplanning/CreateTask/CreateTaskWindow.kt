package com.example.caseplanning.CreateTask

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
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
import butterknife.*
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.MainWindowCasePlanning
import com.example.caseplanning.R
import com.example.caseplanning.TypeTask.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.to_do.*

class CreateTaskWindow : Fragment() {

    var listSubTask: ArrayList<String>? = null
    var listSubTasksView: ArrayList<View>? = null
    private var pageViewModel: MyViewModel? = null
    var textPeriod = ""
    var colorName = ""
    private var btnDeleted: ImageButton? = null
    private var btnOkSubTasks: ImageButton? = null

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.task_window, container, false)

        val toolbar: Toolbar = viewFragment.findViewById(R.id.toolbarCreateTask)

        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Создать"

        listSubTask = arrayListOf()
        listSubTasksView = arrayListOf()

        ButterKnife.bind(this, viewFragment)

        val date = viewFragment.findViewById<TextView>(R.id.setupData)
        pageViewModel!!.day.observe(requireActivity(), Observer { day ->
            if (day != null)
                date.text = "${day.day}.${day.month}.${day.year}"
        })

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

        pageViewModel!!.task.observe(requireActivity(), Observer<Task> { tasks ->
            if (tasks != null)
                task = Task(
                    name = tasks.name,
                    period = tasks.period,
                    replay = tasks.replay,
                    photo = tasks.photo,
                    video = tasks.video,
                    audio = tasks.audio,
                    notification = tasks.notification,
                    listSubTasks = tasks.listSubTasks,
                    color = tasks.color,
                    day = tasks.day,
                    comment = tasks.comment,
                    timer = tasks.timer
                )
        })

        val editTextTask = view.findViewById<EditText>(R.id.editTextTask)
        val textReplay = view.findViewById<TextView>(R.id.textChoose)
        val notification = view.findViewById<TextView>(R.id.reminder)
        val timer = view.findViewById<TextView>(R.id.timer)
        val comment = view.findViewById<EditText>(R.id.comment)
        val day = view.findViewById<TextView>(R.id.setupData)
        if (fragmentManager!!.findFragmentById(R.id.photo) != null) {
            fragmentManager!!.beginTransaction().replace(R.id.photo, Photo()).commit()
        }
        if (fragmentManager!!.findFragmentById(R.id.audio) != null) {
            fragmentManager!!.beginTransaction().replace(R.id.audio, AudioTask()).commit()
        }
        if (fragmentManager!!.findFragmentById(R.id.video) != null) {
            fragmentManager!!.beginTransaction().replace(R.id.video, Video()).commit()
        }


        if (task != null) {

            editTextTask.setText(task!!.name)
            notification.text = task!!.notification
            timer.text = task!!.timer
            comment.setText(task!!.comment)
            day.text = task!!.day

            val radioButtonMorning = view.findViewById<RadioButton>(R.id.radio_morning)
            val radioButtonDay = view.findViewById<RadioButton>(R.id.radio_day)
            val radioButtonEvening = view.findViewById<RadioButton>(R.id.radio_evening)
            val radioButtonOnceAnytime = view.findViewById<RadioButton>(R.id.radio_onceAnytime)
            val color = view.findViewById<ImageButton>(R.id.color)

            when (task!!.color!!) {
                "yellow" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.yellow),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "yellow"
                }
                "green" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.green),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "green"
                }

                "azure" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.azure),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "azure"
                }
                "indigo" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.indigo),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "indigo"
                }
                "orchid" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.orchid),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "orchid"
                }
                "textBlack" -> {
                    color.setColorFilter(
                        resources.getColor(R.color.textBlack),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "textBlack"
                }
            }

            when (task!!.period) {
                radioButtonMorning.text.toString() ->
                    radioButtonMorning.isChecked = true
                radioButtonDay.text.toString() ->
                    radioButtonDay.isChecked = true
                radioButtonEvening.text.toString() ->
                    radioButtonEvening.isChecked = true
                radioButtonOnceAnytime.text.toString() ->
                    radioButtonOnceAnytime.isChecked = true
            }

            if (arguments != null) {
                textReplay.text = arguments!!.getString("Replay")
            } else {
                textReplay.text = task!!.replay
            }

            for (subTask in task!!.listSubTasks!!) {
                listSubTask!!.add(subTask)
            }
            if (listSubTask!!.size > 0) {
                onClickAddSubTask()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.addData)
    fun onClickData() {
        val date = view!!.findViewById<TextView>(R.id.setupData)
        val view = layoutInflater.inflate(R.layout.data_add, null)
        MaterialAlertDialogBuilder(context)
            .setTitle("Выберите дату")
            .setView(view)
            .setPositiveButton("Ок") { dialogInterface, id ->
                val datePicker = view.findViewById<DatePicker>(R.id.dataPicker)
                date.text = "${datePicker.dayOfMonth}.${datePicker.month + 1}.${datePicker.year}"

                Toast.makeText(context, "Дата установлена", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Отмена") { dialogInterface, id ->
                dialogInterface.dismiss()
            }
            .show()
    }

    @OnClick(R.id.color)
    fun onClickColor() {

        val color = view!!.findViewById<ImageButton>(R.id.color)

        val view = layoutInflater.inflate(R.layout.colors, null)
        val colorList = view.findViewById<ListView>(R.id.listColors)
        val colors = arrayListOf<String>(
            "Желтый",
            "Зеленый",
            "Лазурный",
            "Индиго",
            "Орхидея",
            "Черный"
        )
        colorList.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, colors)

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            .setTitle("Выберите цвет")
            .setView(view)
            .setNegativeButton("Отмена") { dialogInterface, id ->
                dialogInterface.dismiss()
            }
            .show()

        colorList.setOnItemClickListener { adapterView, view, position, id ->
            when (position) {
                0 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.yellow),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "yellow"
                }
                1 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.green),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "green"
                }

                2 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.azure),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "azure"
                }
                3 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.indigo),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "indigo"
                }
                4 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.orchid),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "orchid"
                }
                5 -> {
                    color.setColorFilter(
                        resources.getColor(R.color.textBlack),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    colorName = "textBlack"
                }
            }
            materialAlertDialogBuilder.dismiss()
        }
    }

    /*добавление фото задачи*/
    @OnClick(R.id.btnAddPhoto)
    fun onClickAddPhoto() {

        val photo: Fragment = Photo()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.photo, photo)
        transaction.commit()
    }

    /*добавление видео задачи*/
    @OnClick(R.id.btnAddVideo)
    fun onClickAddVideo() {

        val video: Fragment = Video()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.video, video)
        transaction.commit()
    }

    /*добавление аудио задачи*/
    @OnClick(R.id.addAudio)
    fun onClickAddAudio() {

        val audio = AudioTask()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.add(R.id.audio, audio)
        transaction.commit()
    }

    @OnCheckedChanged
        (
        R.id.radio_morning,
        R.id.radio_day,
        R.id.radio_evening,
        R.id.radio_onceAnytime
    )
    fun onRadioButtonCheckChanged(button: CompoundButton, checked: Boolean) {
        if (checked) {
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
    }

    /*динамическое добавление подзадач*/
    @OnClick(R.id.addSubTasks)
    fun onClickAddSubTask() {

        val linerLayoutSubTask = view!!.findViewById<LinearLayout>(R.id.editSubTask)

        var viewFr = layoutInflater.inflate(R.layout.add_sub_tasks, null, false)

        var relativeLayoutSubTasks = viewFr.findViewById<RelativeLayout>(R.id.rel)


        relativeLayoutSubTasks.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (relativeLayoutSubTasks.parent != null) {
            (relativeLayoutSubTasks.parent as ViewGroup).removeView(relativeLayoutSubTasks)
        }

        if (listSubTask!!.size > 0) {

            listSubTasksView = arrayListOf()

            for (subTask in listSubTask!!) {

                viewFr = layoutInflater.inflate(R.layout.add_sub_tasks, null, false)

                relativeLayoutSubTasks = viewFr.findViewById<RelativeLayout>(R.id.rel)

                relativeLayoutSubTasks.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val editTextSubTasks = viewFr.findViewById<EditText>(R.id.editTextSubTasks)

                editTextSubTasks.setText(subTask)

                if (relativeLayoutSubTasks.parent != null) {
                    (relativeLayoutSubTasks.parent as ViewGroup).removeView(
                        relativeLayoutSubTasks
                    )
                }

                btnDeleted = viewFr.findViewById<ImageButton>(R.id.btnDeleted)
                btnDeleted!!.setOnClickListener { onClickDeletedSubTask(viewFr) }

                btnOkSubTasks = viewFr.findViewById<ImageButton>(R.id.btnOkCreate)
                btnOkSubTasks!!.setOnClickListener {
                    onClickCreateSubTask(
                        viewFr,
                        btnDeleted!!,
                        btnOkSubTasks!!
                    )
                }
                btnOkSubTasks!!.performClick()
                listSubTasksView!!.add(viewFr)
                linerLayoutSubTask.addView(relativeLayoutSubTasks)
            }

            listSubTask = arrayListOf()
        } else {

            listSubTasksView!!.add(viewFr)
            linerLayoutSubTask.addView(relativeLayoutSubTasks)

            btnDeleted = viewFr.findViewById<ImageButton>(R.id.btnDeleted)
            btnDeleted!!.setOnClickListener { onClickDeletedSubTask(viewFr) }

            btnOkSubTasks = viewFr.findViewById<ImageButton>(R.id.btnOkCreate)
            btnOkSubTasks!!.setOnClickListener {
                onClickCreateSubTask(
                    viewFr,
                    btnDeleted!!,
                    btnOkSubTasks!!
                )
            }
            val btnAddSubTask = view!!.findViewById<TextView>(R.id.addSubTasks)
            btnAddSubTask.isEnabled = false
        }

        Log.d("Size", "${listSubTasksView!!.size}")
    }

    /*удаляет подзадачу*/
    fun onClickDeletedSubTask(viewSubTask: View) {

        // relativeLayoutSubTasks.visibility = RelativeLayout.GONE
        (viewSubTask.parent as LinearLayout).removeView(viewSubTask)
        listSubTasksView!!.remove(viewSubTask)
        Log.d("Size", "${listSubTasksView!!.size}")

        val btnAddSubTask = view!!.findViewById<TextView>(R.id.addSubTasks)
        btnAddSubTask.isEnabled = true
    }

    /*создает подзадачу*/
    fun onClickCreateSubTask(
        viewFr: View,
        btnDeletedOrEdit: ImageButton,
        btnOkSubTasks: ImageButton
    ) {

        val editTextSubTasks = viewFr.findViewById<EditText>(R.id.editTextSubTasks)
        editTextSubTasks.isEnabled = false

        val btnAddSubTask = view!!.findViewById<TextView>(R.id.addSubTasks)
        btnAddSubTask.isEnabled = true

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

        val btnAddSubTask = view!!.findViewById<TextView>(R.id.addSubTasks)
        btnAddSubTask.isEnabled = false

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

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.timer)
    fun onClickTimer() {

        val timer = view!!.findViewById<TextView>(R.id.timer)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val view = layoutInflater.inflate(R.layout.timer, null)

        val hours = view.findViewById<NumberPicker>(R.id.numpicker_hours)
        hours.maxValue = 59
        hours.value = sharedPreferences.getInt("Hours", 0)

        val minutes = view.findViewById<NumberPicker>(R.id.numpicker_minutes)
        minutes.maxValue = 59
        minutes.value = sharedPreferences.getInt("Minutes", 0)

        val seconds = view.findViewById<NumberPicker>(R.id.numpicker_seconds)
        seconds.maxValue = 59
        seconds.value = sharedPreferences.getInt("Seconds", 0)


        MaterialAlertDialogBuilder(context)
            .setTitle("Таймер")
            .setView(view)
            .setPositiveButton("Установить") { dialogInterface, id ->

                timer.text = "${hours.value}:${minutes.value}:${seconds.value}"
                dialogInterface.dismiss()
            }
            .setNegativeButton("Отмена") { dialogInterface, id ->
                dialogInterface.dismiss()
            }.show()
    }

    @OnClick(R.id.reminder)
    fun onClickNotification() {

        val notification = view!!.findViewById<TextView>(R.id.reminder)
        val dialogFragment = TimePicker(notification)
        dialogFragment.show(fragmentManager!!, "time picker")
    }

    /*получаем данные введенные пользователем в editText и передаем в активити WindowTask*/
    @OnClick(R.id.add)
    fun onclickAdd() {

        val task = saveDataTask()
        val dataBaseTask = DataBaseTask()
        dataBaseTask.createTask(task)

        pageViewModel!!.task.value = null
        pageViewModel!!.uri.value = null
        pageViewModel!!.day.value = null

        val intent = Intent(activity!!.applicationContext, MainWindowCasePlanning()::class.java)
        startActivity(intent)
    }

    private fun saveDataTask(): Task {

        var photoUri: String? = null
        var videoUri: Uri? = null
        var audioUri: String? = null
        var timeAudio: String? = null

        pageViewModel!!.uri.observe(requireActivity(), Observer { uri ->

            if (uri != null) {
                photoUri = uri.photoUri
                videoUri = uri.videoUri
                audioUri = uri.audioUri
                timeAudio = uri.timeAudio

            }
        })

        val editTextTask = view!!.findViewById<EditText>(R.id.editTextTask)
        val textReplay = view!!.findViewById<TextView>(R.id.textChoose)
        val replay = textReplay.text.toString()

        val date = view!!.findViewById<TextView>(R.id.setupData)

        val timer = view!!.findViewById<TextView>(R.id.timer)

        val notification = view!!.findViewById<TextView>(R.id.reminder)

        val comment = view!!.findViewById<EditText>(R.id.comment)


        for (position in 0 until listSubTasksView!!.size) {
            listSubTask!!.add(
                listSubTasksView!![position]
                    .findViewById<EditText>(R.id.editTextSubTasks)
                    .text
                    .toString()
            )
            Log.d("Element", listSubTask!![position])
        }
        return Task(
            name = editTextTask.text.toString(),
            color = colorName,
            period = textPeriod,
            replay = replay,
            day = date.text.toString(),
            timer = timer.text.toString(),
            notification = notification.text.toString(),
            comment = comment.text.toString(),
            listSubTasks = listSubTask!!,
            audio = audioUri,
            timeAudio = timeAudio,
            video = videoUri.toString(),
            photo = photoUri
        )
    }

    override fun onPause() {
        super.onPause()

        val task = saveDataTask()
        pageViewModel!!.task.value = task

        Log.d("OnPAUSE", "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        listSubTask = null
        listSubTasksView = null
        btnDeleted = null
        btnOkSubTasks = null
        pageViewModel = null
    }


}