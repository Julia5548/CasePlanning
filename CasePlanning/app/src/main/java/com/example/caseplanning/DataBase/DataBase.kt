package com.example.caseplanning.DataBase

import android.util.Log
import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.androidhuman.rxfirebase2.database.childEvents
import com.androidhuman.rxfirebase2.database.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Observer
import kotlin.collections.HashMap

class DataBase {

    /*чтение данных из бд*/

    private fun readData(uid: String): Observable<List<Task>> {

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child(uid)
                .child("Tasks")
        /*подключаем класс подписки, оформляем подписчика */
        return object : Observable<List<Task>>() {
            override fun subscribeActual(observer: Observer<in List<Task>>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                val disposal = RxFirebaseDatabase
                    .dataChanges(databaseReference)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val listTaskGenerate: GenericTypeIndicator<HashMap<String, Task>> =
                            object : GenericTypeIndicator<HashMap<String, Task>>() {}
                        val tasks = arrayListOf<Task>()
                        if (dataSnapshot.exists()) {
                            val table = dataSnapshot.getValue(listTaskGenerate)
                            if (table != null) {
                                for (task in table) {
                                    tasks.add(
                                        Task(
                                            name = task.value.name,
                                            listSubTasks = task.value.listSubTasks,
                                            checkedTasks = task.value.checkedTasks,
                                            photo = task.value.photo,
                                            audio = task.value.audio,
                                            timeAudio = task.value.timeAudio,
                                            video = task.value.video,
                                            comment = task.value.comment,
                                            checked = task.value.checked,
                                            timer = task.value.timer,
                                            notification = task.value.notification,
                                            color = task.value.color,
                                            replay = task.value.replay,
                                            period = task.value.period,
                                            day = task.value.day,
                                            idTasks = task.key
                                        )
                                    )
                                }
                            }
                        }
/*получаем очередной список*/
                        observer!!.onNext(tasks)
                    },
                        { throwable ->
                            throwable.printStackTrace()
                        })
            }
        }
    }

    fun readUid(): Observable<List<UID>> {
        return object : Observable<List<UID>>() {
            override fun subscribeActual(observer: Observer<in List<UID>>?) {
                val disposal = RxFirebaseDatabase
                    .dataChanges(FirebaseDatabase.getInstance().reference)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val uids = arrayListOf<UID>()
                        if (dataSnapshot.exists()) {
                            for (uid in dataSnapshot.children) {
                                val uid_user = uid.key
                                uids.add(UID(id = uid_user))
                            }
                        }
                        observer!!.onNext(uids)
                    },
                        { throwable ->
                            throwable.printStackTrace()
                        })

            }
        }
    }

    fun readUser(): Observable<HashMap<String, String>> {
        val databaseReference =
            FirebaseDatabase.getInstance().reference
                .child("Users")
        /*подключаем класс подписки, оформляем подписчика */
        return object : Observable<HashMap<String, String>>() {
            override fun subscribeActual(observer: Observer<in HashMap<String, String>>?) {
                /*подкллючаем RxFirebaseDatabase подключаем изменения данных и подписчиков,
                получаем данные из данных*/
                val disposal = RxFirebaseDatabase
                    .dataChanges(databaseReference)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val listUsers: GenericTypeIndicator<HashMap<String, String>> =
                            object : GenericTypeIndicator<HashMap<String, String>>() {}
                        var users = HashMap<String, String>()
                        if (dataSnapshot.exists()) {
                            users = dataSnapshot.getValue(listUsers)!!
                        }
/*получаем очередной список*/
                        observer!!.onNext(users)
                    },
                        { throwable ->
                            throwable.printStackTrace()
                        })
            }
        }
    }

    private fun readAccess(uid: String) : Observable<HashMap<String, String>>{
        val reference = FirebaseDatabase.getInstance().reference
            .child(uid)
            .child("accessUsers")
        return object : Observable<HashMap<String, String>>(){
            override fun subscribeActual(observer: Observer<in HashMap<String, String>>?) {
                val disposal = RxFirebaseDatabase
                    .dataChanges(reference)
                    .subscribe(fun(dataSnapshot : DataSnapshot){
                        val genericAccess = object : GenericTypeIndicator<HashMap<String, String>>(){}
                        val access = HashMap<String, String>()
                        if(dataSnapshot.exists()){
                            val data = dataSnapshot.getValue(genericAccess)
                            for(( uid_access, user) in data!!){
                                access[uid_access] = user
                            }
                        }
                        observer!!.onNext(access)
                    },
                        {t: Throwable? ->
                            t!!.printStackTrace()
                        })
            }

        }
    }
    private fun readFolder(uid:String): Observable<List<Folder>> {

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child(uid)
                .child("Folders")
        return object : Observable<List<Folder>>() {
            override fun subscribeActual(observer: Observer<in List<Folder>>?) {
                val disposal = RxFirebaseDatabase
                    .dataChanges(databaseReference)
                    .subscribe(fun(dataSnapshot: DataSnapshot) {
                        val genericList =
                            object : GenericTypeIndicator<HashMap<String, Folder>>() {}
                        val folders = arrayListOf<Folder>()
                        if (dataSnapshot.exists()) {
                            val table = dataSnapshot.getValue(genericList)
                            for ((key, value) in table!!) {
                                folders.add(Folder(
                                    id = key,
                                    name = value.name,
                                    tasks = value.tasks,
                                    progress = value.progress,
                                    date = value.date
                                ))
                            }
                        }
                        observer!!.onNext(folders)
                    },
                        { throwable ->
                            throwable.printStackTrace()
                        })
            }
        }
    }

    fun createUser(users: HashMap<String, String>) {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Users")
            .setValue(users)
    }

    fun createTask(task: Task) = FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Tasks")
            .push()
            .setValue(task)

    fun createFolder(uid : String,folder: Folder) = FirebaseDatabase
            .getInstance()
            .reference
            .child(uid)
            .child("Folders")
            .push()
            .setValue(folder)

    fun updateDataUser(access_user: HashMap<String, String>){
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Users")
            .setValue(access_user)
    }
    fun updateAccessUsers(accessUsers : HashMap<String, String>, uid: String){
        FirebaseDatabase
            .getInstance()
            .reference
            .child(uid)
            .child("accessUsers")
            .setValue(accessUsers)
    }

    fun updateDataTask(task: Task, key: String) = FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Tasks")
            .child(key)
            .setValue(task)

    fun updateDataFolder(folder : Folder, key : String, uid: String) = FirebaseDatabase
        .getInstance()
        .reference
        .child(uid)
        .child("Folders")
        .child(key)
        .setValue(folder)

    fun deletedDataTask(key: String) = FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Tasks")
            .child(key)
            .removeValue()

    fun deletedDataFolder(uid: String, key:String) = FirebaseDatabase
            .getInstance()
            .reference
            .child(uid)
            .child("Folders")
            .child(key)
            .removeValue()

    fun retrieveData(uid:String): Observable<List<Task>> {
        return readData(uid)
    }

    fun retrieveAccess(uid: String): Observable<HashMap<String, String>>{
        return readAccess(uid)
    }

    fun retrieveDataUser(): Observable<HashMap<String, String>> {
        return readUser()
    }

    fun retrieveDataUid(): Observable<List<UID>> {
        return readUid()
    }

    fun retrieveDataFolders(uid:String): Observable<List<Folder>> {
        return readFolder(uid)
    }

    fun deletedUser(uid: String) = FirebaseDatabase
    .getInstance()
    .reference
    .child(uid)
    .removeValue()
}