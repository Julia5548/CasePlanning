package com.example.caseplanning

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.Sidebar.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView


class WindowTask : Fragment(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var search: MaterialSearchView
    private val dataBaseTask = DataBaseTask()
    var textTask: String? = " "
    lateinit var listTasks : ListView

    private lateinit var mDrawerLayout : DrawerLayout
    private lateinit var  mToggle : ActionBarDrawerToggle

    var nameUser : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragment = inflater.inflate(R.layout.window_main_tasks, container, false)

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

        val intent: Intent = activity.intent
        textTask = intent.getStringExtra("nameTask")

        val navHeader = navigationView.getHeaderView(0)
        val emailUserText = navHeader.findViewById<TextView>(R.id.emailText)
        val email = "${mAuth.currentUser!!.email}"
        emailUserText.text = email

        listTask(viewFragment)

        return viewFragment
    }

    private fun listTask(viewFragment: View) {


        listTasks = viewFragment.findViewById<ListView>(R.id.listViewTask)

        /*подписываемся и выводим данные из бд, при выходе надо удалить подписчиков*/
        val disposable = dataBaseTask.retrieveData()
            .subscribe {
                val stringList = arrayListOf<String>()

                for(task in it) {
                    stringList.add(task.name)
                }

                val adapter = ArrayAdapter<String>(
                    activity!!.applicationContext,
                    android.R.layout.simple_list_item_1,
                    stringList
                )

                listTasks.adapter = adapter
            }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
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

                transaction.replace(R.id.drawerLayout, groupTask)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            /*доступ к задачам другим людям*/
            R.id.access -> {

                val access: Fragment = Access()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.drawerLayout, access)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment = Progress()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.drawerLayout, progress)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment = Setting()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.drawerLayout, setting)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*техподдержка*/
            R.id.techSupport ->{

                val techSupport: Fragment = TechSupport()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.drawerLayout, techSupport)
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
}


