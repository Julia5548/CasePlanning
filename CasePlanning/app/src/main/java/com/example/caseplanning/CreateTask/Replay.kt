package com.example.caseplanning.CreateTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.example.caseplanning.R

class Replay : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.replay, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbar)

        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Повтор"

        ButterKnife.bind(this, viewFragment)

        listReplay(viewFragment)

        return viewFragment
    }

    fun listReplay(view: View) {

        val listReplay = view.findViewById<ListView>(R.id.listViewReplay)
        val listReplayString = arrayListOf(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
        )

        val adapter = ArrayAdapter<String>(
            activity!!.applicationContext,
            android.R.layout.simple_list_item_1,
            listReplayString
        )

        listReplay.adapter = adapter

        listReplay.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val textView = view as TextView
                val period = textView.text.toString()
                val createTaskWindow =
                    CreateTaskWindow()
                val args = Bundle()

                args.putString("Replay", period)
                createTaskWindow.arguments = args
                fragmentManager!!.beginTransaction().replace(
                    R.id.linerLayout,
                    createTaskWindow).addToBackStack(null).commit()

            }
    }
}