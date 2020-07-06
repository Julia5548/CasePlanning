package com.example.caseplanning.EditElements

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterRecyclerViewFolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class EditFolder(
    val context: Context?,
    folder: Folder,
    val uid: String,
    val mData: ArrayList<Folder>,
    val adapterRecyclerViewFolder: AdapterRecyclerViewFolder
){

    val mFolder = folder

    fun createDialog(){
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val dataBaseTask = DataBase()
        val view = layoutInflater.inflate(R.layout.create_folder, null)
        val outlinedTextField = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
        outlinedTextField.editText!!.setText(mFolder.name)
        outlinedTextField.editText!!.requestFocus(mFolder.name.length)

        MaterialAlertDialogBuilder(context)
            .setTitle("Изменить наименование папки")
            .setView(view)
            .setPositiveButton("Изменить") { dialogInterface, id ->
                val nameNewFolder = outlinedTextField.editText!!.text.toString()
                val edit_folder = Folder(name = nameNewFolder, tasks = mFolder.tasks, progress = mFolder.progress)
                val folder_list = arrayListOf<Folder>()
                val current_task = arrayListOf<Int>()
                val progress_list = arrayListOf<Float>()

                for(folder in mData){

                    if(folder == mFolder){
                        folder_list.add(edit_folder)
                    }else{
                        folder_list.add(folder)
                    }
                    current_task.add(folder.tasks!!.size)
                    progress_list.add(folder.progress.toFloat())
                }

                adapterRecyclerViewFolder.update(folders = folder_list, currentTask = current_task, progress = progress_list)
                dataBaseTask.updateDataFolder(edit_folder, mFolder.id, uid)
                Toast.makeText(context, "Папка $nameNewFolder успешно изменена", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Отменить", null)
            .show()
    }
}
