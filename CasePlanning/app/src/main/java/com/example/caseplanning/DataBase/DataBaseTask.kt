package com.example.caseplanning.DataBase


import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DataBaseTask() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dataBaseReference : DatabaseReference
    private lateinit var textTask:String
    private lateinit var listTask : ArrayList<String>
    private lateinit var listTaskView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    constructor(listTaskView: ListView, textTask:String, adapter: ArrayAdapter<String>) : this() {
        this.listTaskView = listTaskView
        this.textTask = textTask
        this.adapter = adapter
    }


    fun createDataBase(){

        readDataBase()

    }
    fun writeDataBase(listTask: ListView){
        val dataBase = FirebaseDatabase.getInstance()
        dataBaseReference = dataBase.reference
        dataBaseReference.setValue(textTask)

    }

    fun readDataBase(){

        val user = mAuth.currentUser
        dataBaseReference.child(user!!.uid).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(dataBaseError: DatabaseError) {

                Log.d("Tag","Failed to read value", dataBaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val listTaskGenerate:GenericTypeIndicator<ArrayList<String>> = object :
                    GenericTypeIndicator<ArrayList<String>>() {}
                listTask = dataSnapshot.child("Tasks").getValue(listTaskGenerate)!!
                updateUI(adapter,listTaskView)
                val value = dataSnapshot.getValue(String::class.java)
                Log.d("Tag", "Value is: $value ")

            }

        })
    }

    private fun updateUI(adapter: ArrayAdapter<String>, listTaskView: ListView) {

        listTaskView.adapter = adapter

    }
}