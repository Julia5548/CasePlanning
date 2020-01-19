package com.example.caseplanning.DataBase


import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import kotlin.collections.HashMap

class DataBaseTask {

    private  var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private  var taskObservable: Observable<List<Task>>
    private lateinit var disposal: Disposable

    val database = FirebaseDatabase.getInstance()
    val dataBaseReference = database.reference
    val user = mAuth.currentUser

    /*чтение данных из бд*/
    constructor() {

        val ref = dataBaseReference.child(user!!.uid).child("Tasks")
        /*подключаем класс подписки, оформляем подписчика */
        taskObservable = object: Observable<List<Task>>() {
            override fun subscribeActual(observer: Observer<in List<Task>>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                disposal = RxFirebaseDatabase
                    .dataChanges(ref)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val listTaskGenerate: GenericTypeIndicator<HashMap<String, String>> =
                            object : GenericTypeIndicator<HashMap<String, String>>() {}
                        val tasks = arrayListOf<Task>()
                        if (dataSnapshot.exists()) {
                            val table = dataSnapshot.getValue(listTaskGenerate)
                            if (table != null) {
                                for ((key, name) in table) {
                                    tasks.add(Task(id = key, name = name, shouldRepeat = false))
                                }
                            }
                        }
/*получаем очередной список*/
                        observer!!.onNext(tasks);
                    })
            }
        }
    }

    fun createTask(taskText: String) {

        val task  = Task(id = "0", name = taskText, shouldRepeat = false)


        dataBaseReference
            .child(user!!.uid)
            .child("Tasks")
            .push()
            .setValue(taskText)
    }

//    fun updateTask(taskId: Int, taskText: String) {
//        /// dataBaseReference.child(user!!.uid).child("Tasks").push().setValue()
//    }
//
//    fun deleteTask(taskId: Int) {
//
//    }


    fun retrieveData() : Observable<List<Task>> {
        return taskObservable
    }
}