package com.example.caseplanning.mainWindow

import android.graphics.Paint
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Task
import io.reactivex.disposables.Disposable

class CheckedTask() {

    private lateinit var mCardView: CardView
    private lateinit var mTextView: TextView
    private lateinit var mCheckbox : CheckBox

    constructor(cardView: CardView, textView: TextView, checkbox:CheckBox) : this(){
        mCardView = cardView
        mTextView = textView
        mCheckbox = checkbox
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
                        var mTask : Task
                        if(checked) {
                            mTask = Task(
                                name = taskData.name,
                                listSubTasks = taskData.listSubTasks,
                                photo = taskData.photo,
                                audio = taskData.audio,
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