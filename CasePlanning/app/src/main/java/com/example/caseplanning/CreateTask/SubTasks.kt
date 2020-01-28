package com.example.caseplanning.CreateTask

import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class SubTasks : Fragment() {

    fun createSubTask(listSubTask : ListView): ArrayList<String>{

        val listReplayString = arrayListOf(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница","Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница","Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Пятница",
            "Воскресенье"
        )


        return listReplayString
    }




}