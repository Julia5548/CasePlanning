package com.example.caseplanning.DataBase

import com.google.firebase.database.IgnoreExtraProperties
import com.shrikanthravi.collapsiblecalendarview.data.Day
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
data class Task(
    var idTasks: String? = "",
    var name: String? = "",
    var color: String ? = "",
    var day: String ="",
    var period : String = "",
    var listSubTasks:ArrayList<String>? = arrayListOf(),
    var replay : String = "",
    var photo: String ? = "",
    var audio: String ? = "",
    var timeAudio : String? ="",
    var video: String ? = "",
    var comment: String ? = "",
    var timer:String ="",
    var checked :Boolean? = false,
    var notification:String = ""
)