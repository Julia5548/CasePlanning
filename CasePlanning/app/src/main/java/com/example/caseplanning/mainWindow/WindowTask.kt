package com.example.caseplanning.mainWindow

import android.annotation.SuppressLint
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
import androidx.core.view.get
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Setting.Setting
import com.example.caseplanning.Sidebar.*
import com.example.caseplanning.adapter.AdapterSectionTask
import com.example.caseplanning.adapter.SectionHeader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates


class WindowTask : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var search: MaterialSearchView? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var access_users: HashMap<String, String>? = hashMapOf()
    private var disposable: Disposable? = null
    private var date_task by Delegates.notNull<String>()
    private var uid_friends : String? = null

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
        search = viewFragment.findViewById(R.id.search_view)
        search!!.closeSearch()
        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById(R.id.drawerLayout)
        val mToggle = ActionBarDrawerToggle(
            activity, mDrawerLayout, toolbar,
            R.string.Open,
            R.string.Close
        )
        mDrawerLayout!!.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()

        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)
        val dataBase: DataBase? = DataBase()

        val user = FirebaseAuth.getInstance().currentUser!!
        user.let {
            nameUser.text = user.displayName
            emailUser.text = user.email
        }
        var uid : String?
        nameUser.setOnClickListener {
            uid = user.uid
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            uid_friends = null

            calendar(viewFragment, uid!!, "")
        }

        disposable = dataBase!!
            .retrieveAccess(user.uid)
            .subscribe({ user ->
                addAccessUsers(user, navigationView, dataBase)
            },
                { throwable ->
                    throwable.printStackTrace()
                })

         uid = if(arguments != null){
            arguments!!.getString("uid")
        }else{
            FirebaseAuth.getInstance().currentUser!!.uid
        }
        calendar(viewFragment, uid!!, "")

        return viewFragment
    }

    private fun addAccessUsers(
        accessUsers: HashMap<String, String>,
        navigationView: NavigationView?,
        dataBase: DataBase
    ) {
        val menu = navigationView!!.menu
        if (menu.size >= 7) {
            for(position in 6 until menu.size())
                menu[position].subMenu.clear()
        }
        val subMenu = menu.addSubMenu("Пользователи")
        if (accessUsers.isNotEmpty()) {
            for ((user_uid, userName) in accessUsers) {
                subMenu.add(0, 6, 0, userName)
                this.access_users!![user_uid] = userName
                navigationView.invalidate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SimpleDateFormat")
    fun calendar(view: View, uid: String, mDate: String) {

        val calendarView: CollapsibleCalendar = view.findViewById(R.id.linearLayoutCalendar)
        var day = calendarView.selectedDay

        val date_current = if (mDate == "") {
            "${day!!.day}.${day.month}.${day.year}"
        } else {
            mDate
        }
        date_task = "${day!!.day}.${day.month}.${day.year}"
        listTask(view, date_current, uid)

        calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDaySelect() {
                day = calendarView.selectedDay
                val date = "${day!!.day}.${day!!.month + 1}.${day!!.year}"
                date_task = date
                listTask(view, date, uid)
            }

            override fun onClickListener() {}
            override fun onDataUpdate() {}
            override fun onDayChanged() {}
            override fun onItemClick(v: View) {}
            override fun onMonthChange() {}
            override fun onWeekChange(position: Int) {}

        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun listTask(viewFragment: View, date: String, uid: String) {

        var mDate = date
        val arrayDate: List<String> = date.split(".")
        val month: String

        if (arrayDate[1].length == 1) {
            month = "0${arrayDate[1]}"
            mDate = "${arrayDate[0]}.$month.${arrayDate[2]}"
        }
        val day: String
        if (arrayDate[0].length == 1) {
            day = "0${arrayDate[0]}"
            mDate = "$day.${arrayDate[1]}.${arrayDate[2]}"
        }
        val format = SimpleDateFormat("dd.MM.yyyy")
        val getDate = format.parse(mDate)

        val week_format = SimpleDateFormat("EEEE")
        val week = week_format.format(getDate!!)

        val listTasks = viewFragment.findViewById<RecyclerView>(R.id.listViewTask)
        val sections: ArrayList<SectionHeader> = arrayListOf()

        val layoutManager = LinearLayoutManager(activity)
        listTasks!!.layoutManager = layoutManager
        val dataBase: DataBase? = DataBase()
        /*подписываемся и выводим данные из бд*/
        disposable = dataBase!!
            .retrieveData(uid)
            .subscribe({ tasks ->
                val stringListMorning = arrayListOf<Task>()
                val stringListDay = arrayListOf<Task>()
                val stringListEvening = arrayListOf<Task>()
                val stringList = arrayListOf<Task>()
                val checkedList = arrayListOf<Task>()
                for (task in tasks) {
                    if (date == task.day || week == task.replay.toLowerCase(Locale.ROOT)) {
                        if (task.checked!!) {
                            checkedList.add(task)
                        } else {
                            val checkedDate = date.replace('.', '-')
                            if (task.checkedTasks!!.containsKey(checkedDate)) {
                                val value = task.checkedTasks!![checkedDate]!!
                                if (value) {
                                    checkedList.add(task)
                                }
                            } else {
                                when (task.period) {
                                    "Утро" -> stringListMorning.add(task)
                                    "День" -> stringListDay.add(task)
                                    "Вечер" -> stringListEvening.add(task)
                                    "Один раз в любое время" -> stringList.add(task)
                                    else -> stringList.add(task)
                                }
                            }
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

                if (checkedList.isNotEmpty())
                    sections.add(SectionHeader(checkedList, "\r"))

                val adapterSectionTask = AdapterSectionTask(context!!, sections, date, uid)
                listTasks.adapter = adapterSectionTask
            },
                { throwable ->
                    throwable.printStackTrace()
                })
    }

    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.search)
        search!!.setMenuItem(searchItem)

        search!!.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                /*    if(disposable != null && !disposable!!.isDisposed)
                        disposable!!.dispose()*/
            }

            override fun onSearchViewShown() {
                getTask(search)
            }
        })

        search!!.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                newDate(query!!)
                search!!.closeSearch()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun newDate(task_search: String) {
        val dataBase = DataBase()
        disposable = dataBase
            .retrieveData(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe { tasks ->
                for (task in tasks) {
                    if (task_search != "" && task_search == task.name) {
                        val mDate = task.day
                        calendar(
                            view = view!!,
                            uid = FirebaseAuth.getInstance().currentUser!!.uid,
                            mDate = mDate
                        )
                    }
                }
            }
    }

    private fun getTask(searchView: MaterialSearchView?) {
        val dataBase = DataBase()
        val dis = dataBase
            .retrieveData(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe { tasks ->
                val list = arrayOfNulls<String>(tasks.size)
                for ((position, task) in tasks.withIndex()) {
                    list[position] = task.name
                }
                searchView?.setSuggestions(list)
            }
    }

    @Suppress("DEPRECATION")
    @OnClick(R.id.addTask)
    fun onClickBtnAdd() {

        val createTask: Fragment =
            CreateTaskWindow(date_task, null)
        val arg = Bundle()
        if(uid_friends == null) {
            arg.putString("uid", FirebaseAuth.getInstance().currentUser!!.uid)
        }
        else{
            arg.putString("uid", uid_friends)
        }
        createTask.arguments = arg
        fragmentManager!!.beginTransaction().replace(R.id.linerLayout, createTask)
            .addToBackStack(null).commit()

    }

    /*обработчик кнопок меню*/
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment =
                    GroupTask(access_users)
                val arguments = Bundle()
                if(uid_friends != null) {
                    arguments.putString("uid_friends", uid_friends)
                    groupTask.arguments = arguments
                }
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, groupTask)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.tasks -> {
                val windowTask: Fragment =
                    WindowTask()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, windowTask)
                    .addToBackStack(null)
                    .commit()
            }
            /*доступ к задачам другим людям*/
            R.id.access -> {

                val access: Fragment = Access(access_users)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, access)
                    .addToBackStack(null)
                    .commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment =
                    Progress(access_users)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, progress)
                    .addToBackStack(null)
                    .commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment =
                    Setting(access_users)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, setting)
                    .addToBackStack(null)
                    .commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment =
                    TechSupport(access_users)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, techSupport)
                    .addToBackStack(null)
                    .commit()

            }
            /*выход пользователя из системы*/
            R.id.signOut -> {
                if (disposable != null && !disposable!!.isDisposed)
                    disposable!!.dispose()
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent =
                    Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else -> {
                for ((uid, name) in access_users!!) {
                    if (menuItem.title == name) {
                        uid_friends = uid
                        calendar(view!!, uid, "")
                    }
                }
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStop() {
        super.onStop()
        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()
        access_users = null
        search = null
        mDrawerLayout?.closeDrawer(GravityCompat.START)
        mDrawerLayout = null
        Log.d("onStop", "onStop")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()
        access_users = null
        search = null
        mDrawerLayout?.closeDrawer(GravityCompat.START)
        mDrawerLayout = null
    }
}


