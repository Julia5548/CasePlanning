package com.example.caseplanning.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import io.reactivex.disposables.Disposable
import java.lang.Exception


class AdapterSection(val context: Context, data: ArrayList<SectionHeader>) :
    SectionRecyclerViewAdapter<SectionHeader, Task, AdapterSection.SectionViewHolder, AdapterSection.ChildViewHolder>(
        context,
        data
    ) {

    private val mData: ArrayList<SectionHeader> = data
    val dataBaseTask = DataBaseTask()
    var disposable: Disposable? = null

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
                        Toast.makeText(
                            context,
                            "Задача перенесена на завтра",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit -> {
                        Toast.makeText(context, "Задача изменена", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                    R.id.delete -> {
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

         childViewHolder.cardItem.setOnClickListener { view ->
             disposable = dataBaseTask
                 .retrieveData()
                 .subscribe({
                         tasks->
                     for(task in tasks){
                         if (task.name == childViewHolder.dataChild.text){
                             MaterialAlertDialogBuilder(context)
                                 .setTitle(task.name)
                                 .setMessage(
                                     "Период: ${task.period} \n" +
                                             "Повтор: ${task.replay}"
                                 )
                                 .setPositiveButton("Ok"
                                 ) { dialogInterface, p1 ->
                                     dialogInterface.dismiss()
                                     disposable!!.isDisposed
                                 }
                                 .show()
                         }
                     }
                 },
                     {trowable->
                         trowable.printStackTrace()
                     })
         }

    }

}