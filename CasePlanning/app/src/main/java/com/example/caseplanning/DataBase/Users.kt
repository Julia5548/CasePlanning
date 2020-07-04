package com.example.caseplanning.DataBase

import android.text.BoringLayout

data class Users (
    var name: String? = "",
    var accessUsers : HashMap<String, String> = hashMapOf()
)