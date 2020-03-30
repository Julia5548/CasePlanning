package com.example.caseplanning.DataBase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task (
    var id: String ? = "",
    var name: String,
    var nameSubTasks:ArrayList<String>? = arrayListOf(),
    var photo: String ? = "",
    var audio: String ? = "",
    var video: String ? = "",
    var text: String ? = "",
    var color: String ? = "",
    var day: String ? = "",
    var timeStart: String ? = "",
    var timeEnd: String ? = "",
    var shouldRepeat: Boolean
)