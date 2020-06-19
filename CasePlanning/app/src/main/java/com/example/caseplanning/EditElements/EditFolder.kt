package com.example.caseplanning.EditElements

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Folder
import com.example.caseplanning.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class EditFolder (val context : Context?, folder: Folder){

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
                val folder = Folder(name = nameNewFolder, tasks = mFolder.tasks, progress = mFolder.progress)

                dataBaseTask.updateDataFolder(folder, mFolder.id)
                Toast.makeText(context, "Папка $nameNewFolder успешно изменена", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Отменить", null)
            .show()
    }
}
