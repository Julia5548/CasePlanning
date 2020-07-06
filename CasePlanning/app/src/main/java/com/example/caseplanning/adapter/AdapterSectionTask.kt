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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.CheckedTask
import com.example.caseplanning.mainWindow.FragmentDialog
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList


class AdapterSectionTask(
    val context: Context,
    data: ArrayList<SectionHeader>,
    day: String?,
    uid: String
) :
    SectionRecyclerViewAdapter<SectionHeader, Task, AdapterSectionTask.SectionViewHolder, AdapterSectionTask.ChildViewHolder>(
        context,
        data
    ) {

    private val mData: ArrayList<SectionHeader> = data
    var disposable: Disposable? = null
    val dataBaseTask = DataBase()
    val mDay: String? = day
    val mUid: String = uid

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dataChild: TextView = itemView.findViewById(R.id.nameItemList)
        var cardItem = itemView.findViewById<MaterialCardView>(R.id.card_view)
        val txtOptionDigit = itemView.findViewById<TextView>(R.id.txtOptionDigit)
        val color_task = itemView.findViewById<ImageView>(R.id.color_task)
        val checkedTask = itemView.findViewById<MaterialCheckBox>(R.id.checkbox)
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

        if (task.color != "")
            drawColorTask(task.color!!, childViewHolder)

        if (task.checked != null && task.checked!!) {
            val checkedTask = CheckedTask(
                childViewHolder.cardItem,
                childViewHolder.dataChild,
                childViewHolder.checkedTask
            )
            checkedTask.checkedTask(task.checked!!)
        }
        val checkedDate = mDay?.replace('.', '-')
        if (task.checkedTasks!!.isNotEmpty() && task.checkedTasks!!.containsKey(checkedDate)) {
            val checkedTask = CheckedTask(
                childViewHolder.cardItem,
                childViewHolder.dataChild,
                childViewHolder.checkedTask
            )
            checkedTask.checkedTask(task.checkedTasks!!.get(checkedDate!!)!!)
        }


        childViewHolder.cardItem.setOnClickListener{ viewHolder ->
            if (task.day == mDay) {
                FragmentDialog(task).show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "Dialog"
                )
            }
        }

        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()

        childViewHolder.checkedTask.setOnCheckedChangeListener{ buttonView, isChecked ->
            val checkedTask = CheckedTask(
                childViewHolder.cardItem,
                childViewHolder.dataChild,
                childViewHolder.checkedTask
            )
            if (task.replay != "Нет >") {
                checkedTask.updateReplayTask(task, isChecked, mDay!!, mUid)
            } else {
                checkedTask.updateTask(task, isChecked, mUid)
            }
            mData.removeAll(mData)
            notifyDataChanged(mData)
        }

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
                            tomorrowTask(task, nextDate)

                        }
                        Toast.makeText(
                            context,
                            "Задача перенесена на завтра",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit -> {
                        val editTask: Fragment = EditTask(task)
                        val arg = Bundle()
                        arg.putString("uid", mUid)
                        editTask.arguments = arg
                        (context as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .remove(WindowTask())
                            .replace(R.id.linerLayout, editTask)
                            .commit()
                        true
                    }
                    R.id.delete -> {
                        dataBaseTask.deletedDataTask(task.idTasks!!, mUid)
                        mData.removeAll(mData)
                        notifyDataChanged(mData)
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

    }

    private fun drawColorTask(
        color: String,
        childViewHolder: ChildViewHolder
    ) {
        when (color) {
            "yellow" -> childViewHolder.color_task.setImageResource(R.color.yellow)
            "green" -> childViewHolder.color_task.setImageResource(R.color.green)
            "azure" -> childViewHolder.color_task.setImageResource(R.color.azure)
            "indigo" -> childViewHolder.color_task.setImageResource(R.color.indigo)
            "orchid" -> childViewHolder.color_task.setImageResource(R.color.orchid)
            "textBlack" -> childViewHolder.color_task.setImageResource(R.color.textBlack)
            else -> childViewHolder.color_task.visibility = ImageView.INVISIBLE
        }
    }

    private fun tomorrowTask(task: Task, nextDate: String) {
        task.day = nextDate
        dataBaseTask.updateDataTask(task, task.idTasks!!, mUid)

        mData.removeAll(mData)
        notifyDataChanged(mData)
    }
}
