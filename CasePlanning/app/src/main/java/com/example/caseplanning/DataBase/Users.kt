package com.example.caseplanning.DataBase

data class Users (
    var name: String? = "",
    var email: String ? = "",
    var accessUsers : ArrayList<String> = arrayListOf()
)