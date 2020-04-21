package com.example.caseplanning.DataBase

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlin.collections.HashMap

class DataBaseTask {

    /*чтение данных из бд*/
    private fun readData() : Observable<List<Task>> {

        val databaseReference = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("Tasks")
        /*подключаем класс подписки, оформляем подписчика */
        return object: Observable<List<Task>>() {
            override fun subscribeActual(observer: Observer<in List<Task>>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                val disposal = RxFirebaseDatabase
                    .dataChanges(databaseReference)
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

    fun readUid() : Observable<List<UID>>{
        return object : Observable<List<UID>>(){
            override fun subscribeActual(observer: Observer<in List<UID>>?) {
                val disposal = RxFirebaseDatabase
                    .dataChanges(FirebaseDatabase.getInstance().reference)
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

    fun readUser() : Observable<Users>{
        val databaseReference = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("Users")
        /*подключаем класс подписки, оформляем подписчика */
        return object: Observable<Users>() {
            override fun subscribeActual(observer: Observer<in Users>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                val disposal = RxFirebaseDatabase
                    .dataChanges(databaseReference)
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

    private fun readFolder() : Observable<List<Folder>>{

        val databaseReference = FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().currentUser!!.uid).child("Folders")
        return object : Observable<List<Folder>>(){
            override fun subscribeActual(observer: Observer<in List<Folder>>?) {
             val disposal = RxFirebaseDatabase
                 .dataChanges(databaseReference)
                 .subscribe(fun (dataSnapshot : DataSnapshot){
                     val genericList = object:GenericTypeIndicator<HashMap<String,Folder>>(){}
                     val folders = arrayListOf<Folder>()
                     if(dataSnapshot.exists()){
                         val table = dataSnapshot.getValue(genericList)
                         for ((_, value) in table!!){
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
        FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Users")
            .setValue(users)
    }

    fun createTask(task:Task) {

        FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Tasks")
            .push()
            .setValue(task)
    }

    fun createFolder(folder : Folder){
        FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Folders")
            .push()
            .setValue(folder)
    }

    fun retrieveData() : Observable<List<Task>> {

        return readData()
    }

    fun retrieveDataUser() : Observable<Users> {

        return readUser()
    }

    fun retrieveDataUid() : Observable<List<UID>> {

        return  readUid()
    }

    fun retrieveDataFolders() : Observable<List<Folder>>{

        return readFolder()
    }

}