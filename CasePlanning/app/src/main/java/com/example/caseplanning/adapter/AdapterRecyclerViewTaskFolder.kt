package com.example.caseplanning.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.CheckedTask

class AdapterRecyclerViewTaskFolder(
    val context: Context,
    data: ArrayList<Task>,
    folder: Folder?
) : RecyclerView.Adapter<AdapterRecyclerViewTaskFolder.ViewHolder>() {

    val mData: ArrayList<Task> = data
    val mFolder = folder

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameItem = itemView.findViewById<TextView>(R.id.nameItemList)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_folder_task_subTask)
        val cardView = itemView.findViewById<CardView>(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.folder_tass_list, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameItem.text = mData[position].name

        if (mData[position].checked!!) {
            val checkedTask = CheckedTask(holder.cardView, holder.nameItem, holder.checkBox)
            checkedTask.checkedTask()
        }

        holder.checkBox.setOnCheckedChangeListener { card, isChecked ->
            checkedFolderTask(
                mData[position].name!!,
                holder.cardView,
                holder.nameItem,
                holder.checkBox,
                isChecked
            )
            try {
                notifyItemChanged(position)
            } catch (e: Exception) {
                Log.d("TAG", e.toString())
            }
        }
    }

    private fun checkedFolderTask(
        nameTask: String,
        cardView: CardView,
        textView: TextView,
        checkBox: CheckBox,
        checked: Boolean
    ) {
        if (mFolder != null) {
            for (task_folder in mFolder.tasks!!) {
                if (task_folder.name == nameTask) {
                    val checkedTask = CheckedTask(cardView, textView, checkBox)
                    checkedTask.updateFolder(task_folder, checked)
                }
            }
        }
    }
}