package com.example.caseplanning.DataBase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task (
    var name: String? = "",
    var color: String ? = "",
    var day: String ? = "",
    var period : String = "",
    var nameSubTasks:ArrayList<String>? = arrayListOf(),
    var replay : String = "",

    var photo: String ? = "",
    var audio: String ? = "",
    var video: String ? = "",
    var text: String ? = "",


    var timeStart: String ? = "",
    var timeEnd: String ? = "",
    var shouldRepeat: Boolean = false
)