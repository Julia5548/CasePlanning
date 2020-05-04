package com.example.caseplanning.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.EditElements.PhotoEdit
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.FragmentDialog
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import io.reactivex.disposables.Disposable
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class AdapterSection(val context: Context, data: ArrayList<SectionHeader>, day: String?, fragmentManager: FragmentManager) :
    SectionRecyclerViewAdapter<SectionHeader, Task, AdapterSection.SectionViewHolder, AdapterSection.ChildViewHolder>(
        context,
        data
    ) {

    private val mData: ArrayList<SectionHeader> = data
    var disposable: Disposable? = null
    val dataBaseTask = DataBaseTask()
    val mDay: String? = day


    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dataChild: TextView = itemView.findViewById(R.id.nameItemList)
        var cardItem = itemView.findViewById<CardView>(R.id.card_view)
        val txtOptionDigit = itemView.findViewById<TextView>(R.id.txtOptionDigit)
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dataSection: TextView = itemView.findViewById(R.id.sectionData)
    }

    override fun onCreateSectionViewHolder(parent: ViewGroup?, viewType: Int): SectionViewHolder {

        val view =
            LayoutInflater.from(context).inflate(R.layout.item_listview_header, parent, false)

        return SectionViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_list, parent, false)

        return ChildViewHolder(view)
    }

    override fun onBindSectionViewHolder(
        sectionViewHolder: SectionViewHolder?,
        sectionPosition: Int,
        sectionHeader: SectionHeader?
    ) {
        sectionViewHolder!!.dataSection.text = sectionHeader!!.section
    }

    @SuppressLint("RestrictedApi")
    override fun onBindChildViewHolder(
        childViewHolder: ChildViewHolder?,
        sectionPosition: Int,
        childPosition: Int,
        task: Task?
    ) {

        childViewHolder!!.dataChild.text = task!!.name

        childViewHolder.txtOptionDigit.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, childViewHolder.txtOptionDigit)
            popupMenu.inflate(R.menu.menu_list_task)
            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.tomorrow -> {
                        if (mDay != null) {
                            val calendar = Calendar.getInstance()
                            val countDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                            val date = mDay.split(".")
                            var tomorrowDay = date[0].toInt() + 1
                            var month = date[1].toInt()
                            var year = date[2].toInt()
                            var nextDate = "${tomorrowDay}.${month}.${year}"
                            if (countDay == 31 && tomorrowDay == 32
                                || countDay == 30 && tomorrowDay == 31
                                || countDay == 28 && month == 2 && tomorrowDay == 29
                                || countDay == 29 && month == 2 && tomorrowDay == 30
                            ) {
                                tomorrowDay = 1
                                month += 1
                                nextDate = "${tomorrowDay}.${month}.${year}"
                            }
                            if (month == 12 && tomorrowDay == 32) {
                                tomorrowDay = 1
                                month += 1
                                year += 1
                                nextDate = "${tomorrowDay}.${month}.${year}"
                            }
                            disposable = dataBaseTask
                                .retrieveData()
                                .subscribe { tasks ->
                                    for (taskData in tasks) {
                                        if (taskData.name == task.name && taskData.day == task.day) {
                                            val mTask = Task(
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
                                                day = nextDate
                                            )
                                            dataBaseTask.updateDataTask(mTask, taskData.idTasks!!)
                                        }
                                    }

                                    mData.removeAll(mData)
                                    notifyDataChanged(mData)
                                }

                        }
                        Toast.makeText(
                            context,
                            "Задача перенесена на завтра",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit -> {
                        val editTask: Fragment = EditTask()
                        val arg = Bundle()
                        val arrayListTask = arrayListOf(task.name, task.day)
                        arg.putStringArrayList("dataTask", arrayListTask)
                        editTask.arguments = arg

                        val manager = (context as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.linerLayout, editTask)
                            .commit()
                        true
                    }
                    R.id.delete -> {

                        val disposable = dataBaseTask
                            .retrieveData()
                            .subscribe { tasks ->
                                for (taskData in tasks) {
                                    if (taskData.name == task.name && taskData.day == task.day) {
                                        dataBaseTask.deletedDataTask(taskData.idTasks!!)
                                    }
                                }
                                mData.removeAll(mData)
                                notifyDataChanged(mData)
                            }
                        Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                    else -> false
                }
            }

            try {
                val fieldPopupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopupMenu.isAccessible = true
                val mPopup = fieldPopupMenu.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (exception: Exception) {
                Log.d("Main", "Error showing menu icons")
            }

            popupMenu.show()
        }


        childViewHolder.cardItem.setOnClickListener { viewHolder ->

            val disposable = dataBaseTask
                .retrieveData()
                .subscribe({ tasks ->

                    for (taskData in tasks) {
                        if (taskData.name == task.name && taskData.day == task.day) {

                            FragmentDialog(taskData).show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "Dialog"
                            )

                        }
                    }
                },
                    { trowable ->
                        trowable.printStackTrace()
                    })
        }

        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()

    }
}
