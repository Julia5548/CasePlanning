package com.example.caseplanning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.Sidebar.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.events.Subscriber
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar


class WindowTask : Fragment(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var search: MaterialSearchView
    private val dataBaseTask = DataBaseTask()
    var textTask: String? = " "
    lateinit var listTasks : ListView
     var list: ArrayList<String>? = null
    var adapter : ArrayAdapter<String>? = null
    private var subsribe : Subscriber? = null
    private lateinit var mDrawerLayout : DrawerLayout
    private lateinit var  mToggle : ActionBarDrawerToggle
    private lateinit var pageViewModel : MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val viewFragment = inflater.inflate(R.layout.window_main_task, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarTask)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Главное меню"

        ButterKnife.bind(this, viewFragment)

        mAuth = FirebaseAuth.getInstance()

        /*кнопка поиска*/
        search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById(R.id.drawerLayout)
        mToggle = ActionBarDrawerToggle(activity, mDrawerLayout,
            R.string.Open, R.string.Close)
        mDrawerLayout.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()
        /*добавление стрелки для закрытия бокового меню, делает ее кликабельной*/
        actionBar.setDisplayHomeAsUpEnabled(true)

        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

      /*  val intent: Intent = activity.intent
        textTask = intent.getStringExtra("nameTask")*/

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)


        var disposable  = dataBaseTask
            .retrieveDataUser()
            .subscribe{
                user->
                nameUser.text = user.name
                emailUser.text = user.email
            }

        val email_find = "negodyaeva.yulya@gmail.com"
        val list1 = arrayListOf<String>()
        disposable = dataBaseTask
            .retrieveDataUid()
            .subscribe{
                uids->
                    for(uid in uids){
                        list1.add(uid.id!!)
                    }
                for (uid_user in list1) {
                    FirebaseDatabase.getInstance()
                        .reference
                        .child(uid_user)
                        .child("Users")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("Error", "Error trying to get classified ads for")
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val user = dataSnapshot.getValue(Users::class.java)
                                    if (email_find == user!!.email){
                                        Log.d("эта хрень", "существует")
                                    }else{
                                        Log.d("эта хрень", "no существует")
                                    }

                                } else {
                                    Log.d("ошибка", "пользователя не существует")
                                }
                            }
                        })
                }
            }
        val userRecord = FirebaseAuth.getInstance()


        val calendarView: CollapsibleCalendar = viewFragment.findViewById(R.id.linearLayoutCalendar)

        listTask(viewFragment)

        return viewFragment
    }

    private fun listTask(viewFragment: View) {


        listTasks = viewFragment.findViewById<ListView>(R.id.listViewTask)

        /*подписываемся и выводим данные из бд, при выходе надо удалить подписчиков*/
        val disposable = dataBaseTask
            .retrieveData()
            .subscribe {
             task ->
                val stringList = arrayListOf<String>()

                for(tasks in task) {
                    stringList.add(tasks.name!!)
                }

         adapter = ArrayAdapter<String>(
                    activity!!.applicationContext,
                    android.R.layout.simple_list_item_multiple_choice,
                    stringList
                )

                listTasks.adapter = adapter
        registerForContextMenu(listTasks)
        listTasks.onItemClickListener = this
            }

    }

    /*появление кнопок при нажатие на элемент из листа*/
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = activity!!.menuInflater
        inflater.inflate(R.menu.menu_task, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info : AdapterView.AdapterContextMenuInfo =  item.menuInfo as AdapterView.AdapterContextMenuInfo
        val nameTask = listTasks.getItemAtPosition(info.position).toString()
        when (item.itemId) {
            R.id.edit -> {
                val task  = Task(name = nameTask, nameSubTasks = null, shouldRepeat = true)
                pageViewModel.setTask(task)
                //действия при изменении
                val editTask : Fragment = EditTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, editTask)
                transaction.addToBackStack(null)
                transaction.commit()
                return true
            }
            R.id.deleted -> {
                list!!.removeAt(0)
                adapter!!.notifyDataSetChanged()
                return true
            }

        }
        return super.onContextItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        pageViewModel  = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.search)
        search.setMenuItem(searchItem)
        search.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                /*поиск задач*/
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*поиск*/
                return false
            }

        })
        search.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                /*поиск*/
                /* currentText = ""
                 search.setAudioList(mData)*/
            }

            override fun onSearchViewShown() {
                // search.setQuery(currentText,false)
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    @OnClick(R.id.addTask)
    fun onClickBtnAdd() {

        val createTask: Fragment =
            CreateTaskWindow()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, createTask)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /*обработчик кнопок меню*/
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        val id = menuItem.itemId

        when(id){
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment = GroupTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, groupTask)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            /*доступ к задачам другим людям*/
            R.id.access -> {

                val access: Fragment = Access()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, access)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment = Progress()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, progress)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment = Setting()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, setting)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*техподдержка*/
            R.id.techSupport ->{

                val techSupport: Fragment = TechSupport()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, techSupport)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*выход пользователя из системы*/
            R.id.signOut -> {
                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return  true
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onDestroy() {
        super.onDestroy()
    }


}


