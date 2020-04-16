package com.example.caseplanning.DataBase

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlin.collections.HashMap

class DataBaseTask {

    private  var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var taskObservable: Observable<List<Task>>
    private lateinit var userObservable : Observable<Users>
    private lateinit var folderObservable : Observable<List<Folder>>
    private lateinit var uid : Observable<List<UID>>
    private lateinit var disposal: Disposable


    val database = FirebaseDatabase.getInstance()
    val dataBaseReference = database.reference
    val user = mAuth.currentUser!!

    /*чтение данных из бд*/
    private fun readData() {

        val ref = dataBaseReference.child(user.uid).child("Tasks")
        /*подключаем класс подписки, оформляем подписчика */
        taskObservable = object: Observable<List<Task>>() {
            override fun subscribeActual(observer: Observer<in List<Task>>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                disposal = RxFirebaseDatabase
                    .dataChanges(ref)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val listTaskGenerate: GenericTypeIndicator<HashMap<String,Task>> =
                            object : GenericTypeIndicator<HashMap<String,Task>>() {}
                        val tasks = arrayListOf<Task>()
                        if (dataSnapshot.exists()) {
                            val table = dataSnapshot.getValue(listTaskGenerate)
                            if (table != null) {
                                for (task in table) {
                                    tasks.add(Task(name = task.value.name, replay = task.value.replay, period = task.value.period, day = task.value.day))
                                }
                            }
                        }
/*получаем очередной список*/
                        observer!!.onNext(tasks)
                    },
                        {
                                throwable->
                            throwable.printStackTrace()
                        })
            }
        }
    }

    fun readUid(){
         uid = object : Observable<List<UID>>(){
            override fun subscribeActual(observer: Observer<in List<UID>>?) {
                disposal = RxFirebaseDatabase
                    .dataChanges(dataBaseReference)
                    .subscribe(fun (dataSnapshot : DataSnapshot){
                        val uids = arrayListOf<UID>()
                        if (dataSnapshot.exists()) {
                            for (uid in dataSnapshot.children){
                                val uid_user = uid.key
                                uids.add(UID(id=uid_user))
                                }
                            }
                            observer!!.onNext(uids)
                    },
                        {
                                throwable->
                            throwable.printStackTrace()
                        })

            }
        }
    }

    fun readUser(){
        val ref = dataBaseReference.child(user.uid).child("Users")
        /*подключаем класс подписки, оформляем подписчика */
        userObservable = object: Observable<Users>() {
            override fun subscribeActual(observer: Observer<in Users>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                disposal = RxFirebaseDatabase
                    .dataChanges(ref)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        var users  = Users()
                        if (dataSnapshot.exists()) {
                            users = dataSnapshot.getValue(Users::class.java)!!
                            }
/*получаем очередной список*/
                        observer!!.onNext(users)
                    },
                        {
                                throwable->
                            throwable.printStackTrace()
                        })
            }
        }
    }

    private fun readFolder(){

        val folder = dataBaseReference.child(user.uid).child("Folders")
        folderObservable = object : Observable<List<Folder>>(){
            override fun subscribeActual(observer: Observer<in List<Folder>>?) {
             disposal = RxFirebaseDatabase
                 .dataChanges(folder)
                 .subscribe(fun (dataSnapshot : DataSnapshot){
                     val genericList = object:GenericTypeIndicator<HashMap<String,Folder>>(){}
                     val folders = arrayListOf<Folder>()
                     if(dataSnapshot.exists()){
                         val table = dataSnapshot.getValue(genericList)
                         for ((key, value) in table!!){
                             folders.add(value)
                         }
                     }
                     observer!!.onNext(folders)
                 },
                     {
                             throwable->
                         throwable.printStackTrace()
                     })
            }
        }
    }

    fun createUser(name:String?, email:String?){
        val  users = Users(name = name, email = email)
        dataBaseReference
            .child(user.uid)
            .child("Users")
            .setValue(users)
    }

    fun createTask(task:Task) {

        dataBaseReference
            .child(user.uid)
            .child("Tasks")
            .push()
            .setValue(task)
    }

    fun createFolder(folder : Folder){
        dataBaseReference
            .child(user.uid)
            .child("Folders")
            .push()
            .setValue(folder)
    }
//    fun updateTask(taskId: Int, taskText: String) {
//        /// dataBaseReference.child(user!!.uid).child("Tasks").push().setValue()
//    }
//
//    fun deleteTask(taskId: Int) {
//
//    }


    fun retrieveData() : Observable<List<Task>> {
        readData()
        return taskObservable
    }

    fun retrieveDataUser() : Observable<Users> {
        readUser()
        return userObservable
    }

    fun retrieveDataUid() : Observable<List<UID>> {
        readUid()
        return uid
    }

    fun retrieveDataFolders() : Observable<List<Folder>>{
        readFolder()
        return folderObservable
    }

    fun dispose(){
            disposal.dispose()
    }
}