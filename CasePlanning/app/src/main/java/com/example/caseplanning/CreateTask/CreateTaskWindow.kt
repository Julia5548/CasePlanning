package com.example.caseplanning.CreateTask

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import butterknife.OnTouch
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.Video
import com.example.caseplanning.Increase.PhotoIncrease
import com.example.caseplanning.Increase.VideoIncrease
import com.example.caseplanning.R
import com.example.caseplanning.TypeTask.AudioTask
import com.example.caseplanning.TypeTask.Photo
import com.example.caseplanning.mainWindow.MainWindowCasePlanning
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class CreateTaskWindow(val date_task: String?, mTask : Task?) : Fragment() {

    var listSubTask: ArrayList<String>? = null
    var listSubTasksView: ArrayList<View>? = null
    private var pageViewModel: MyViewModel? = null
    var textPeriod = ""
    var colorName = ""
    var task: Task? = mTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // retainInstance = true
        pageViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.task_window, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Создать"

        listSubTask = arrayListOf()
        listSubTasksView = arrayListOf()

        ButterKnife.bind(this, view)

        val date = view.findViewById<TextView>(R.id.setupData)
        date.text = date_task!!

        return view
    }

    override fun onStart() {
        super.onStart()
        restore_data(view!!)
    }

    private fun restore_data(view: View) {

        pageViewModel?.task?.observe(requireActivity(), Observer<Task> { tasks ->
            if(tasks != null)
                task = tasks
        })

        val editTextTask = view.findViewById<EditText>(R.id.editTextTask)
        val textReplay = view.findViewById<TextView>(R.id.textChoose)
        val notification = view.findViewById<TextView>(R.id.reminder)
        val timer = view.findViewById<TextView>(R.id.timer)
        val comment = view.findViewById<EditText>(R.id.comment)
        val day = view.findViewById<TextView>(R.id.setupData)

        if (task != null) {

            editTextTask.setText(task!!.name)

            if (task!!.notification != "")
                notification.text = task!!.notification

            if (task!!.timer != "")
                timer.text = task!!.timer

            if (arguments != null) {
                textReplay.text = arguments!!.getString("Replay")
            } else {
                textReplay.text = task!!.replay
            }

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
                else -> {
                    colorName = ""
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
                else -> textPeriod = ""
            }

            if (task!!.listSubTasks!!.size > 0)
                restoreSubTask()

            if (task!!.photo != "" || task!!.video != "" || task!!.audio != "")
                loadMedia()
        }
    }

    private fun loadMedia() {

        if (task!!.photo != null && task!!.photo != "") {
            val relativeLayout: RelativeLayout = view!!.findViewById(R.id.photo_image)
            relativeLayout.visibility = View.VISIBLE
            var imageView: ImageView? = null
            if (imageView != null) {
                (imageView.drawable as? BitmapDrawable)!!.bitmap.recycle()
            }
            imageView = view!!.findViewById<ImageButton>(R.id.photoImage)

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inSampleSize = 42
            val bitmap = BitmapFactory.decodeFile(task!!.photo!!, options)
            imageView!!.setImageBitmap(bitmap)
        }

        if (task!!.video != null && task!!.video != "") {
            val relativeLayout: RelativeLayout = view!!.findViewById(R.id.video)
            relativeLayout.visibility = View.VISIBLE
            val video = view!!.findViewById<VideoView>(R.id.videoView)
            video.setVideoURI(task!!.video!!.toUri())
            video.seekTo(1)
        }

        if (task!!.audio != null && task!!.audio != "") {
            val audio: Fragment = AudioTask(task, null)
            fragmentManager!!.beginTransaction().add(R.id.audio, audio).commit()
        }
    }

    @OnClick(R.id.photoImage)
    fun photo_zoom() {
        val photoIncrease: Fragment = PhotoIncrease(task)
        fragmentManager!!.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.linerLayout, photoIncrease)
            .commit()
    }

    @OnTouch(R.id.videoView)
    fun video_zoom() {
        val videoIncrease: Fragment = VideoIncrease(task, "create_task")
        fragmentManager!!.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.linerLayout, videoIncrease)
            .commit()
    }

    private fun restoreSubTask() {
        if (task!!.listSubTasks!!.size > 0) {
            listSubTasksView = arrayListOf()

            val linerLayoutSubTask = view!!.findViewById<LinearLayout>(R.id.editSubTask)

            for (subTask in task!!.listSubTasks!!) {

                val inflate = LayoutInflater.from(context)
                val view_sub = inflate.inflate(R.layout.add_sub_tasks, null, false) as ViewGroup
                val relativeLayoutSubTasks = view_sub.findViewById<RelativeLayout>(R.id.rel)
                val btnDeleted = view_sub.findViewById<ImageButton>(R.id.btnDeleted)
                val btnOkSubTasks = view_sub.findViewById<ImageButton>(R.id.btnOkCreate)

                relativeLayoutSubTasks!!.layoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val edit = relativeLayoutSubTasks.findViewById<EditText>(R.id.editTextSubTasks)
                edit.setText(subTask)

                btnDeleted!!.setOnClickListener { onClickDeletedSubTask(view_sub) }

                btnOkSubTasks!!.setOnClickListener {
                    onClickCreateSubTask(
                        view_sub,
                        btnDeleted,
                        btnOkSubTasks
                    )
                }
                btnOkSubTasks.performClick()

                linerLayoutSubTask!!.addView(relativeLayoutSubTasks)
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
                else -> {
                    colorName = ""
                }
            }
            materialAlertDialogBuilder.dismiss()
        }
    }

    /*добавление фото задачи*/
    @OnClick(R.id.btnAddPhoto)
    fun onClickAddPhoto() {
        var photo = fragmentManager!!.findFragmentByTag("photo")
        if (photo == null) {
            task = saveDataTask()
            photo = Photo(task, "create_task")
            fragmentManager!!.beginTransaction().add(photo, "photo").commit()
        }
    }

    /*добавление видео задачи*/
    @OnClick(R.id.btnAddVideo)
    fun onClickAddVideo() {
        var video = fragmentManager!!.findFragmentByTag("video")
        if (video == null) {
            task = saveDataTask()
            video = Video(task, "create_task")
            fragmentManager!!.beginTransaction().add(video, "video").commit()
        }
    }

    /*добавление аудио задачи*/
    @OnClick(R.id.addAudio)
    fun onClickAddAudio() {
        task = saveDataTask()
        val audio = AudioTask(task, pageViewModel)
        fragmentManager!!.beginTransaction().add(R.id.audio, audio).commit()
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

        val textReplay = view!!.findViewById<TextView>(R.id.textChoose)
        val view = layoutInflater.inflate(R.layout.replay, null)
        val replayList = view.findViewById<ListView>(R.id.listViewReplay)
        val replay = arrayListOf<String>(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
        )
        replayList.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, replay)

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            .setTitle("Выберите день недели")
            .setView(view)
            .setNegativeButton("Отмена") { dialogInterface, id ->
                dialogInterface.dismiss()
            }
            .show()

        replayList.setOnItemClickListener { adapterView, view, position, id ->
            textReplay.text = replayList.getItemAtPosition(position).toString()
            materialAlertDialogBuilder.dismiss()
        }
    }

    /*динамическое добавление подзадач*/
    @OnClick(R.id.addSubTasks)
    fun onClickAddSubTask() {

        val btnAddSubTask = view!!.findViewById<TextView>(R.id.addSubTasks)
        val linerLayoutSubTask = view!!.findViewById<LinearLayout>(R.id.editSubTask)
        val view = layoutInflater.inflate(R.layout.add_sub_tasks, null, false)
        val relativeLayoutSubTasks = view.findViewById<RelativeLayout>(R.id.rel)
        val btnDeleted = view.findViewById<ImageButton>(R.id.btnDeleted)
        val btnOkSubTasks = view.findViewById<ImageButton>(R.id.btnOkCreate)

        relativeLayoutSubTasks.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (relativeLayoutSubTasks.parent != null) {
            (relativeLayoutSubTasks.parent as ViewGroup).removeView(relativeLayoutSubTasks)
        }
        linerLayoutSubTask.addView(relativeLayoutSubTasks)

        btnDeleted!!.setOnClickListener { onClickDeletedSubTask(view) }

        btnOkSubTasks!!.setOnClickListener {
            onClickCreateSubTask(
                view,
                btnDeleted,
                btnOkSubTasks
            )
        }
        btnAddSubTask.isEnabled = false
    }

    /*удаляет подзадачу*/
    fun onClickDeletedSubTask(viewSubTask: View) {

        // relativeLayoutSubTasks.visibility = RelativeLayout.GONE
        (viewSubTask.parent as LinearLayout).removeView(viewSubTask)
        if(listSubTasksView!!.contains(viewSubTask))
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
        listSubTasksView!!.add(viewFr)

        Log.d("Size", "${listSubTasksView!!.size}")
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

                timer.text = if (hours.value.toString() == "0") {
                    String.format("%02d:%02d", minutes.value, seconds.value)
                } else {
                    String.format("%02d:%02d:%02d", hours.value, minutes.value, seconds.value)
                }
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
    @SuppressLint("SimpleDateFormat")
    @OnClick(R.id.add)
    fun onclickAdd() {

        val task = saveDataTask()

        // createNotificationChannel()
        //  scheduleNotification(task.day, task.notification, task.name!!)

        val dataBaseTask = DataBase()
        dataBaseTask.createTask(task)

        pageViewModel?.task?.value = null

        val intent = Intent(context, MainWindowCasePlanning::class.java)
        startActivity(intent)
    }

    /* @SuppressLint("SimpleDateFormat")
     private fun scheduleNotification(day: String, timeNotification: String, name: String) {

         var mDate = day
         val intentNotification = Intent(context!!, NotificationBroadcast::class.java)
         intentNotification.putExtra("name_task", name)
         intentNotification.putExtra("time_notification", timeNotification)
         val pendingIntent = PendingIntent.getBroadcast(
             context!!,
             42,
             intentNotification,
             PendingIntent.FLAG_ONE_SHOT
         )
         val alarmManager: AlarmManager = context!!.getSystemService(ALARM_SERVICE) as AlarmManager
         val arrayDate: List<String> = mDate.split(".")
         var month = ""
         if (arrayDate[1].length == 1) {
             month = "0${arrayDate[1]}"
             mDate = "${arrayDate[0]}.$month.${arrayDate[2]}"
         }
         val date_notification = "$mDate $timeNotification"
         val date = SimpleDateFormat("dd.MM.yyyy hh:mm").parse(date_notification)
         val milliseconds = date!!.time

         alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent)
     }
 */
    /*   private fun createNotificationChannel() {

           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
               val name : CharSequence = "LemubitReminderChannel"
               val description = "Channel for Lemubit Reminder"
               val importance = NotificationManager.IMPORTANCE_DEFAULT
               val channel = NotificationChannel("notification", name, importance)
               channel.description = description

               val notificationManager:NotificationManager = context!!.getSystemService(NotificationManager::class.java)!!
               notificationManager.createNotificationChannel(channel)
           }
       }
   */
    private fun saveDataTask(): Task {


        var photoUri: String? = ""
        var videoUri: String? = ""
        var audioUri: String? = ""
        var timeAudio: String? = ""

        if (task != null) {
            photoUri = task!!.photo
            audioUri = task!!.audio
            timeAudio = task!!.timeAudio
            videoUri = task!!.video
        }

        val editTextTask = view!!.findViewById<EditText>(R.id.editTextTask)
        val textReplay = view!!.findViewById<TextView>(R.id.textChoose)
        val replay = textReplay.text.toString()
        val date = view!!.findViewById<TextView>(R.id.setupData)
        val timer = view!!.findViewById<TextView>(R.id.timer)
        val notification = view!!.findViewById<TextView>(R.id.reminder)
        val comment = view!!.findViewById<EditText>(R.id.comment)

        if (listSubTasksView == null) {
            listSubTasksView = arrayListOf()
        }
        if (listSubTask == null) {
            listSubTask = arrayListOf()
        }
        for (position in 0 until listSubTasksView!!.size) {
            listSubTask!!.add(
                listSubTasksView!![position]
                    .findViewById<EditText>(R.id.editTextSubTasks)
                    .text
                    .toString()
            )
            Log.d("Element", listSubTask!![position])
        }


        val checked_list = hashMapOf<String, Boolean>()
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
            checkedTasks = checked_list,
            audio = audioUri,
            timeAudio = timeAudio,
            video = videoUri,
            photo = photoUri
        )
    }

    override fun onPause() {
        super.onPause()
        listSubTask = arrayListOf()
        val task = saveDataTask()
        pageViewModel?.task?.value = task
        Log.d("Onpause", "onPause")
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onDestroyView", "onDestroyView")
        listSubTask = null
        pageViewModel = null
        listSubTasksView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("OnDestroy", "onDestroy")
        task = null
        listSubTask = null
        listSubTasksView = null
        pageViewModel = null
    }

    override fun onDetach() {
        super.onDetach()
    }
}