package com.example.caseplanning.EditElements

import android.content.Context
import android.view.LayoutInflater
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.R
import java.lang.Exception

class EditFolder (val context : Context?,
                  val activity: FragmentActivity?,
                 val adapter:  ArrayAdapter<String>?,
                 var list : ArrayList<String>,
                 var id : Int){


    fun createDialog(name : String?){
         val layoutInflater : LayoutInflater = LayoutInflater.from(context)

        val pageViewModel  = ViewModelProviders.of(activity!!).get(MyViewModel::class.java)
        val view = layoutInflater.inflate(R.layout.edit_folder, null)
        val mBuilder = AlertDialog.Builder(activity)
        val nameFolder = view.findViewById<EditText>(R.id.editNameFolder)
        nameFolder.setText(name)
        val position = nameFolder.text.length
        nameFolder.setSelection(position)

        mBuilder.setView(view)

        mBuilder.setPositiveButton(
            "Изменить"
        ) { dialog, id ->

            try {
                val nameNewFolder = nameFolder.text.toString()
                Toast.makeText(context, "Наименование папки успешно изменено", Toast.LENGTH_SHORT)
                    .show()
                pageViewModel.mName.value = nameNewFolder
                list[this.id] = nameNewFolder
                adapter!!.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

            }
        }
        mBuilder.show()

    }
}
