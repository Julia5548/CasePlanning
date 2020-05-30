package com.example.caseplanning.DataBase

data class Folder(
    var name : String = "",
    var id : String = "",
    var tasks: ArrayList<Task>? = arrayListOf()
)