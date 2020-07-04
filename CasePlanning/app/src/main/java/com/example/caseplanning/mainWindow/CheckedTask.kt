package com.example.caseplanning.mainWindow

import android.content.Context
import android.graphics.Paint
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import java.text.SimpleDateFormat
import java.util.*

class CheckedTask() {

    private lateinit var mCardView: CardView
    private lateinit var mTextView: TextView
    private lateinit var mCheckbox: CheckBox
    private lateinit var mContext: Context

    constructor(context: Context):this(){
        mContext = context
    }
    constructor(cardView: CardView, textView: TextView, checkbox: CheckBox) : this() {
        mCardView = cardView
        mTextView = textView
        mCheckbox = checkbox
    }

    fun updateFolder(
        task: Task,
        checked: Boolean,
        folder: Folder?,
        position: Int,
        uid : String
    ) {

        val database = DataBase()
        if (folder!!.tasks!!.contains(task)) {
            task.checked = checked
            folder.tasks!![position] = task

            val calendar= Calendar.getInstance()
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formatedDate = format.format(calendar.time)

            folder.date = formatedDate
            var checked_count = 0.0
            for (folder_task in folder.tasks!!) {
                if (folder_task.checked!!) {
                    checked_count++
                }
            }
            //Расчет для прогресса
            val mProgress = (checked_count / folder.tasks!!.size) * 100
            folder.progress = mProgress.toString()
            database.updateDataFolder(folder, folder.id, uid)
        }
    }

    fun updateReplayTask(task: Task, checked: Boolean, day: String) {
        val database = DataBase()
        val checkedDate = day.replace('.', '-')
        if (checked) {
            task.checkedTasks!![checkedDate] = checked
        } else {
            task.checkedTasks!!.remove(checkedDate)
        }
        database.updateDataTask(task, task.idTasks!!)
    }

    fun updateTask(
        task: Task,
        checked: Boolean
    ) {
        val dataBase = DataBase()
        if (checked) {
            task.checked = checked
        } else {
            task.checked = checked
        }
        dataBase.updateDataTask(task, task.idTasks!!)
    }

    fun checkedTask(checked: Boolean) {
        if (checked) {
            mCardView.isEnabled = false
            mCheckbox.isChecked = checked
            mTextView.isEnabled = false
            mTextView.paintFlags = mTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            mCardView.isEnabled = true
            mCheckbox.isChecked = checked
            mTextView.isEnabled = true
            mTextView.paintFlags = 0
        }
    }
}