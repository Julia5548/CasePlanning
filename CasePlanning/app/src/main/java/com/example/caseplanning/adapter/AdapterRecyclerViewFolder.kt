package com.example.caseplanning.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.EditElements.EditFolder
import com.example.caseplanning.GroupTask.ListTaskGroup
import com.example.caseplanning.R
import com.google.android.material.card.MaterialCardView
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class AdapterRecyclerViewFolder(
    val context: Context,
    data: ArrayList<Folder>,
    currentTask: ArrayList<Int>,
    progress: ArrayList<Float>,
    val uid: String
) : RecyclerView.Adapter<AdapterRecyclerViewFolder.ViewHolder>() {

    val mData: ArrayList<Folder> = data
    val mCurrentTask: ArrayList<Int> = currentTask
    val mProgress = progress

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameItem = itemView.findViewById<TextView>(R.id.nameItemList)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.card_view)
        val currentTask = itemView.findViewById<TextView>(R.id.current_task)
        val progress_made = itemView.findViewById<CircularProgressBar>(R.id.progress_made)
        val percent_progress = itemView.findViewById<TextView>(R.id.percent_progress_made)
        val menu = itemView.findViewById<TextView>(R.id.menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_folder, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    fun update(
        folders: ArrayList<Folder>,
        currentTask: ArrayList<Int>,
        progress: ArrayList<Float>
    ) {
        this.mData.clear()
        this.mData.addAll(folders)

        this.mCurrentTask.clear()
        this.mCurrentTask.addAll(currentTask)

        this.mProgress.clear()
        this.mProgress.addAll(progress)
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameItem.text = mData[position].name
        holder.currentTask.text = mCurrentTask[position].toString()
        holder.progress_made.apply {
            setProgressWithAnimation(mProgress[position], 1000)
            progressMax = 100f
        }
        holder.percent_progress.text = "${mProgress[position].toInt()} %"

        holder.menu.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.menu)
            popupMenu.inflate(R.menu.menu_folder)
            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.edit -> {
                        val editFolder = EditFolder(context, mData[position], uid)
                        editFolder.createDialog()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.cardView.setOnClickListener {
            val listTaskGroup: Fragment = ListTaskGroup(
                mData[position], uid
            )
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.linerLayout, listTaskGroup).addToBackStack(null)
                .commit()
        }

    }
}