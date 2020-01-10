package com.example.caseplanning.DataBase


import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DataBaseTask() {

    private  var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    val dataBaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var textTask:String? = null
    val user = mAuth.currentUser

    private lateinit var listTask : List<String>
    private lateinit var listTaskView: ListView

    private lateinit var mAdapter : FirebaseListAdapter<String>
    private lateinit var adapter : ArrayAdapter<String>

    constructor(listTaskView: ListView, textTask:String?, adapter: ArrayAdapter<String>) : this() {
        this.listTaskView = listTaskView
        this.adapter = adapter
        this.textTask = textTask
    }
    constructor(listTaskView: ListView) : this() {
        this.listTaskView = listTaskView
    }

    fun writeDataBase() {

        mAdapter = object : FirebaseListAdapter<String>(
            AppCompatActivity(), String::class.java,
            android.R.layout.simple_list_item_1, dataBaseReference.child(user!!.uid)
                .child("Tasks")
        ){
            override fun populateView(v: View?, model: String?, position: Int) {
                Log.d("Что то делает", "хз что, пока не понимаю")
            }
        }
        listTaskView.adapter = mAdapter
    }

    fun readDataBase(){

        dataBaseReference.child(user!!.uid).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(dataBaseError: DatabaseError) {

                Log.d("Tag","Failed to read value", dataBaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val listTaskGenerate:GenericTypeIndicator<List<String>> = object :
                    GenericTypeIndicator<List<String>>() {}
                listTask = dataSnapshot.child("Tasks").getValue(listTaskGenerate)!!
                updateUI(adapter,listTaskView)


            }

        })
    }

    private fun updateUI(adapter: ArrayAdapter<String>, listTaskView: ListView) {

        listTaskView.adapter = adapter
    }

    fun onClickAddTask(textTask: String?){

        dataBaseReference.child(user!!.uid).child("Tasks").push().setValue(textTask)
    }
}
