package com.example.caseplanning.DataBase

import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DataBaseTask {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dataBaseReference : DatabaseReference


    fun writeDataBase(listTask: ListView){
        val dataBase = FirebaseDatabase.getInstance()
        dataBaseReference = dataBase.reference

        dataBaseReference.setValue(listTask)
    }

    fun readDataBase(){

        dataBaseReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(dataBaseError: DatabaseError) {

                Log.d("Tag","Failed to read value", dataBaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val value = dataSnapshot.getValue(String::class.java)
                Log.d("Tag", "Value is: $value ")

            }

        })
    }
}