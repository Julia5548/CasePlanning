package com.example.caseplanning.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.CheckedTask
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import java.lang.Exception

class AdapterRecyclerViewTaskFolder(
    val context: Context,
    data: ArrayList<Task>,
    folder: Folder?,
    val uid: String
) : RecyclerView.Adapter<AdapterRecyclerViewTaskFolder.ViewHolder>() {

    val mData: ArrayList<Task> = data
    var mFolder = folder

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameItem = itemView.findViewById<TextView>(R.id.nameItemList)
        val checkBox = itemView.findViewById<MaterialCheckBox>(R.id.checkbox_folder_task_subTask)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.folder_tass_list, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    fun update(
        task: ArrayList<Task>,
        folder: Folder?
    ) {
        this.mData.clear()
        this.mData.addAll(task)

        mFolder = null
        mFolder = folder

        try {
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameItem.text = mData[position].name

        val checkedTask = CheckedTask(holder.cardView, holder.nameItem, holder.checkBox)
        checkedTask.checkedTask(mData[position].checked!!)


        holder.checkBox.setOnCheckedChangeListener { card, isChecked ->
            checkedFolderTask(
                mData[position].name!!,
                isChecked
            )
        }
    }

    private fun checkedFolderTask(
        nameTask: String,
        checked: Boolean
    ) {
        if (mFolder != null) {
            for ((position, task_folder) in mFolder!!.tasks!!.withIndex()) {
                if (task_folder.name == nameTask) {
                    val checkedTask = CheckedTask(context)
                    checkedTask.updateFolder(task_folder, checked, mFolder, position, uid)
                }
            }
        }
        update(mFolder!!.tasks!!, mFolder)
    }
}