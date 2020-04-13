package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import io.reactivex.disposables.Disposable

class AdapterSection(val context: Context, data: ArrayList<SectionHeader>) :
    SectionRecyclerViewAdapter<SectionHeader, Task, AdapterSection.SectionViewHolder, AdapterSection.ChildViewHolder>(
        context,
        data
    ) {

    private val mData: ArrayList<SectionHeader> = data
    val dataBaseTask = DataBaseTask()
    var disposable : Disposable? =  null

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dataChild: TextView = itemView.findViewById(R.id.nameItemList)
        var cardItem = itemView.findViewById<CardView>(R.id.card_view)
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val dataSection : TextView = itemView.findViewById(R.id.sectionData)
    }

    override fun onCreateSectionViewHolder(parent: ViewGroup?, viewType: Int): SectionViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_listview_header, parent, false)

        return SectionViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_list, parent, false)

        return ChildViewHolder(view)
    }

    override fun onBindSectionViewHolder(sectionViewHolder: SectionViewHolder?, sectionPosition: Int, sectionHeader: SectionHeader?) {
        sectionViewHolder!!.dataSection.text = sectionHeader!!.section
    }

    override fun onBindChildViewHolder(childViewHolder: ChildViewHolder?, sectionPosition: Int, childPosition: Int, task: Task?) {

        childViewHolder!!.dataChild.text = task!!.name

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