package com.example.caseplanning.CreateTask


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.DataBase.UriTypeTask


class MyViewModel : ViewModel() {

    val mName = MutableLiveData<String>()
    val task = MutableLiveData<Task>()
    val uri = MutableLiveData<UriTypeTask>()

    fun setUri(uri: UriTypeTask){
        this.uri.value = uri
    }
    fun getUri() : LiveData<UriTypeTask> = uri

    fun setnameFolder(nameFolder: String) {

        mName.value = nameFolder
    }

    fun getNameFolder(): LiveData<String> = mName

    fun setTask(task: Task) {
        this.task.value = task
    }

    fun getTask(): LiveData<Task> = task

}