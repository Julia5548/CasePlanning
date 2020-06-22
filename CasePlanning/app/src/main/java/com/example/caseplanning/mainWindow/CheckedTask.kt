package com.example.caseplanning.mainWindow

import android.graphics.Paint
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable

class CheckedTask() {

    private lateinit var mCardView: CardView
    private lateinit var mTextView: TextView
    private lateinit var mCheckbox : CheckBox
    private var folder_list : ArrayList<Folder>? = null

    constructor(cardView: CardView, textView: TextView, checkbox:CheckBox) : this(){
        mCardView = cardView
        mTextView = textView
        mCheckbox = checkbox
    }

    private fun getListFolder() : ArrayList<Folder>? = folder_list

     fun updateFolder(task: Task, checked: Boolean) {
        folder_list = arrayListOf()
         val database = DataBase()
        var disposable: Disposable? = null
        disposable = database
            .retrieveDataFolders()
            .subscribe { folders ->
                for (folder in folders) {
                    if (!folder.tasks.isNullOrEmpty()) {
                        folder_list!!.add(folder)
                    }
                }
                if(task.replay != "Нет >"){
                    updateReplayTask(
                        mUid = FirebaseAuth.getInstance().currentUser!!.uid,
                        task = task,
                        checked = checked)
                }else {
                    updateTask(
                        mUid = FirebaseAuth.getInstance().currentUser!!.uid,
                        task = task,
                        checked = checked
                    )
                }
                onDestroy(disposable)
            }
    }

    private fun updateReplayTask(mUid: String, task: Task, checked : Boolean) {
        val dataBase = DataBase()
        var disposable: Disposable? = null
        disposable = dataBase
            .retrieveData(mUid)
            .subscribe { tasks ->
                for (taskData in tasks) {
                    if (taskData.name == task.name && taskData.replay != "Нет >") {
                        val list = getListFolder()
                        var mTask: Task
                        if (checked) {
                            val checkedDate = task.day.replace('.', '-')
                            taskData.checkedTasks!![checkedDate] = checked
                            mTask = Task(
                                name = taskData.name,
                                listSubTasks = taskData.listSubTasks,
                                checkedTasks = taskData.checkedTasks,
                                photo = taskData.photo,
                                audio = taskData.audio,
                                idTasks = task.idTasks,
                                timeAudio = taskData.timeAudio,
                                video = taskData.video,
                                comment = taskData.comment,
                                timer = taskData.timer,
                                notification = taskData.notification,
                                color = taskData.color,
                                replay = taskData.replay,
                                period = taskData.period,
                                checked = false,
                                day = task.day
                            )
                        } else {
                            val checkedDate = task.day.replace('.', '-')
                            taskData.checkedTasks!!.remove(checkedDate)
                            mTask = Task(
                                name = taskData.name,
                                listSubTasks = taskData.listSubTasks,
                                checkedTasks = taskData.checkedTasks,
                                photo = taskData.photo,
                                audio = taskData.audio,
                                idTasks = task.idTasks,
                                timeAudio = taskData.timeAudio,
                                video = taskData.video,
                                comment = taskData.comment,
                                timer = taskData.timer,
                                notification = taskData.notification,
                                color = taskData.color,
                                replay = taskData.replay,
                                period = taskData.period,
                                checked = checked,
                                day = task.day
                            )
                        }
                        dataBase.updateDataTask(mTask, taskData.idTasks!!)

                        for (folder in list!!) {
                            if (folder.tasks!!.contains(taskData)) {
                                folder.tasks!!.remove(taskData)
                                folder.tasks!!.add(mTask)
                                var checked_count = 0.0
                                for (folder_task in folder.tasks!!) {
                                    if (folder_task.checkedTasks!!.containsValue(checked)) {
                                        checked_count++
                                    }
                                }
                                //Расчет для прогресса
                                val mProgress = (checked_count / folder.tasks!!.size) * 100

                                val update_folder = Folder(
                                    name = folder.name,
                                    tasks = folder.tasks!!,
                                    progress = mProgress.toString()
                                )
                                dataBase.updateDataFolder(update_folder, folder.id)
                            }
                        }
                        onDestroy(disposable)
                    }
                }
            }
    }

    fun updateTask(
        mUid: String,
        task: Task,
        checked: Boolean
    ){
        val dataBase = DataBase()
        var disposable: Disposable? = null
        disposable = dataBase
            .retrieveData(mUid)
            .subscribe { tasks ->
                for (taskData in tasks) {
                    if (taskData.name == task.name && taskData.day == task.day) {
                        val list = getListFolder()
                        var mTask : Task
                        if(checked) {
                            mTask = Task(
                                name = taskData.name,
                                listSubTasks = taskData.listSubTasks,
                                photo = taskData.photo,
                                audio = taskData.audio,
                                idTasks = task.idTasks,
                                timeAudio = taskData.timeAudio,
                                video = taskData.video,
                                comment = taskData.comment,
                                timer = taskData.timer,
                                notification = taskData.notification,
                                color = taskData.color,
                                replay = taskData.replay,
                                period = taskData.period,
                                checked = checked,
                                day = task.day
                            )
                        }else{
                            mTask = Task(
                                name = taskData.name,
                                listSubTasks = taskData.listSubTasks,
                                photo = taskData.photo,
                                audio = taskData.audio,
                                idTasks = task.idTasks,
                                timeAudio = taskData.timeAudio,
                                video = taskData.video,
                                comment = taskData.comment,
                                timer = taskData.timer,
                                notification = taskData.notification,
                                color = taskData.color,
                                replay = taskData.replay,
                                period = taskData.period,
                                checked = checked,
                                day = task.day
                            )
                        }
                        dataBase.updateDataTask(mTask, taskData.idTasks!!)

                        for(folder in list!!) {
                            if (folder.tasks!!.contains(taskData)) {
                                folder.tasks!!.remove(taskData)
                                folder.tasks!!.add(mTask)
                                var checked_count = 0.0
                                for(folder_task in folder.tasks!!){
                                    if(folder_task.checked!!){
                                        checked_count++
                                    }
                                }
                                //Расчет для прогресса
                                val mProgress = (checked_count/folder.tasks!!.size) * 100

                                val update_folder = Folder(name = folder.name, tasks = folder.tasks!!, progress = mProgress.toString())
                                dataBase.updateDataFolder(update_folder, folder.id)
                            }
                        }
                        onDestroy(disposable)
                    }
                }
            }
    }

    private fun onDestroy(disposable : Disposable?){
        if(disposable != null && !disposable.isDisposed)
            disposable.dispose()
    }
    fun checkedTask() {
        mCardView.isEnabled = false
        mCheckbox.isChecked = true
        mTextView.isEnabled = false
        mTextView.paintFlags = mTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}