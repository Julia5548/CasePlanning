package com.example.caseplanning.mainWindow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.DataBaseTask
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Sidebar.*
import com.example.caseplanning.adapter.AdapterSectionTask
import com.example.caseplanning.adapter.SectionHeader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import io.reactivex.disposables.Disposable


class WindowTask : Fragment(), NavigationView.OnNavigationItemSelectedListener {


    private var search: MaterialSearchView? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var access_users : ArrayList<String>? = arrayListOf<String>()
    private var task_list: ArrayList<Task>? = arrayListOf<Task>()
    private lateinit var pageViewModel: MyViewModel
    private lateinit var disposable: Disposable
    private var currentText : String? = null
    private lateinit var adapterSectionTask: AdapterSectionTask

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val viewFragment = inflater.inflate(R.layout.window_main_task, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarTask)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Главное меню"

        ButterKnife.bind(this, viewFragment)

        /*кнопка поиска*/
        search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search!!.closeSearch()

        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById(R.id.drawerLayout)
        mToggle = ActionBarDrawerToggle(
            activity, mDrawerLayout,
            R.string.Open,
            R.string.Close
        )
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        /*проверяем состояние*/
        mToggle!!.syncState()
        /*добавление стрелки для закрытия бокового меню, делает ее кликабельной*/
        actionBar.setDisplayHomeAsUpEnabled(true)

        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)
        val dataBaseTask: DataBaseTask? = DataBaseTask()

        disposable = dataBaseTask!!
            .retrieveDataUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe({ user ->
                nameUser.text = user.name
                emailUser.text = user.email
                access_users = user.accessUsers
                addAccessUsers(user.accessUsers, navigationView, dataBaseTask)
            },
                { throwable ->
                    throwable.printStackTrace()
                })

        calendar(viewFragment, FirebaseAuth.getInstance().currentUser!!.uid)

        return viewFragment
    }

    private fun getListAccessUsers(): ArrayList<String> = access_users!!
    private fun addAccessUsers(
        accessUsers: ArrayList<String>,
        navigationView: NavigationView?,
        dataBaseTask: DataBaseTask
    ) {
        val menu = navigationView!!.menu
        val subMenu = menu.addSubMenu("Пользователи")

        if (accessUsers.isNotEmpty()) {
            for (user_uid in accessUsers) {
                disposable = dataBaseTask
                    .retrieveDataUser(user_uid)
                    .subscribe { user ->
                        subMenu.add(user.name)
                        navigationView.invalidate()
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    fun calendar(view: View, uid: String) {

        val calendarView: CollapsibleCalendar = view.findViewById(R.id.linearLayoutCalendar)
        var day = calendarView.selectedDay

        val date_current = "${day!!.day}.${day.month}.${day.year}"
        listTask(view, date_current, uid)

        pageViewModel.day.value = date_current

        calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDaySelect() {
                day = calendarView.selectedDay
                val date = "${day!!.day}.${day!!.month + 1}.${day!!.year}"
                listTask(view, date, uid)
                pageViewModel.day.value = date
            }
            override fun onClickListener() {}
            override fun onDataUpdate() {}
            override fun onDayChanged() {}
            override fun onItemClick(v: View){}
            override fun onMonthChange(){}
            override fun onWeekChange(position: Int) {}

        })
    }

    private fun listTask(viewFragment: View, date: String, uid: String) {

        val listTasks = viewFragment.findViewById<RecyclerView>(R.id.listViewTask)
        val sections: ArrayList<SectionHeader> = arrayListOf()

        val layoutManager = LinearLayoutManager(context)
        listTasks!!.layoutManager = layoutManager
        val dataBaseTask: DataBaseTask? = DataBaseTask()
        /*подписываемся и выводим данные из бд*/
        disposable = dataBaseTask!!
            .retrieveData(uid)
            .subscribe({ task ->
                val stringListMorning = arrayListOf<Task>()
                val stringListDay = arrayListOf<Task>()
                val stringListEvening = arrayListOf<Task>()
                val stringList = arrayListOf<Task>()

                for (tasks in task) {
                    task_list!!.add(tasks)
                    if (date == tasks.day) {
                        when (tasks.period) {
                            "Утро" -> stringListMorning.add(
                                Task(
                                    name = tasks.name!!,
                                    day = tasks.day,
                                    color = tasks.color
                                )
                            )
                            "День" -> stringListDay.add(
                                Task(
                                    name = tasks.name!!,
                                    day = tasks.day,
                                    color = tasks.color
                                )
                            )
                            "Вечер" -> stringListEvening.add(
                                Task(
                                    name = tasks.name!!,
                                    day = tasks.day,
                                    color = tasks.color
                                )
                            )
                            "Один раз в любое время" -> stringList.add(
                                Task(
                                    name = tasks.name!!,
                                    day = tasks.day,
                                    color = tasks.color
                                )
                            )
                            else -> stringList.add(
                                Task(
                                    name = tasks.name!!,
                                    day = tasks.day,
                                    color = tasks.color
                                )
                            )
                        }
                    }
                }

                if (stringList.isNotEmpty())
                    sections.add(SectionHeader(stringList, "В любое время"))

                if (stringListMorning.isNotEmpty())
                    sections.add(SectionHeader(stringListMorning, "Утро"))

                if (stringListEvening.isNotEmpty())
                    sections.add(SectionHeader(stringListEvening, "Вечер"))

                if (stringListDay.isNotEmpty())
                    sections.add(SectionHeader(stringListDay, "День"))

                adapterSectionTask = AdapterSectionTask(context!!, sections, date, uid)
                listTasks.adapter = adapterSectionTask
            },
                { throwable ->
                    throwable.printStackTrace()
                })
    }

    private fun getListTask() : ArrayList<Task> = task_list!!
    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.search)
        search?.setMenuItem(searchItem)

        search?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                /*поиск задач*/
                currentText = query
                adapterSectionTask.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*поиск
                currentText = newText
                if(currentText == "")
                    adapterSectionTask.setTaskList(task_list)
                adapterSectionTask.filter.filter(newText)*/
                return false
            }

        })
        search!!.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                /*поиск
                 currentText = ""
                 adapterSectionTask.setTaskList(task_list)*/
            }

            override fun onSearchViewShown() {
                search!!.setQuery(currentText, false)
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
        if (mToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /*обработчик кнопок меню*/
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment =
                    GroupTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, groupTask)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.tasks -> {
                val windowTask: Fragment =
                    WindowTask()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, windowTask)
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
            R.id.techSupport -> {

                val techSupport: Fragment = TechSupport()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, techSupport)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*выход пользователя из системы*/
            R.id.signOut -> {
                val mAuth = FirebaseAuth.getInstance()

                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else -> {
                val dataBaseTask = DataBaseTask()
                val list_users = getListAccessUsers()
                if (!list_users.isEmpty()) {
                    for (user_uid in list_users) {
                        disposable = dataBaseTask
                            .retrieveDataUser(user_uid)
                            .subscribe { user ->
                                if (menuItem.title == user.name!!)
                                    calendar(view!!, user_uid)
                            }
                    }
                }
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStop() {
        super.onStop()
        super.onPause()
        if (!disposable.isDisposed)
            disposable.dispose()
        task_list = null
        access_users = null
        currentText = null
        mToggle = null
        search = null
        mDrawerLayout?.closeDrawer(GravityCompat.START)
        mDrawerLayout = null
        Log.d("onStop", "onStop")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (!disposable.isDisposed)
            disposable.dispose()
        mToggle = null
        task_list = null
        access_users = null
        currentText = null
        search = null
        mDrawerLayout?.closeDrawer(GravityCompat.START)
        mDrawerLayout = null
    }

}


