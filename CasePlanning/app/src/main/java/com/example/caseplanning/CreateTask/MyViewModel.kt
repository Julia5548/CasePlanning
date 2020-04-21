package com.example.caseplanning.CreateTask


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caseplanning.DataBase.Day
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.DataBase.UriTypeTask


data class MyViewModel(

    var mName: MutableLiveData<String> = MutableLiveData(),
    var task: MutableLiveData<Task> = MutableLiveData(),
    var uri: MutableLiveData<UriTypeTask> = MutableLiveData(),
    var day : MutableLiveData<Day> = MutableLiveData()

) : ViewModel()
