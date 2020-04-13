package com.example.caseplanning.adapter

import com.example.caseplanning.DataBase.Task
import com.intrusoft.sectionedrecyclerview.Section

class SectionHeader (childList : ArrayList<Task>, section : String): Section<Task> {

    val mDataChildList : ArrayList<Task> = childList
    val section = section

    override fun getChildItems(): ArrayList<Task> = mDataChildList
}