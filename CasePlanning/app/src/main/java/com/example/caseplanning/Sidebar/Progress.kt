package com.example.caseplanning.Sidebar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Setting.Setting
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*

class Progress : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mDrawerLayout: DrawerLayout? = null
    var disposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.progress_made, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Прогресс выполнения задач"

        ButterKnife.bind(this, view)

        /*боковое меню*/
        mDrawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayout)
        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)

        val mToggle = ActionBarDrawerToggle(
            activity, mDrawerLayout, toolbar,
            R.string.Open, R.string.Close
        )

        mDrawerLayout!!.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()

        val user = FirebaseAuth.getInstance().currentUser!!
        user.let {
            nameUser.text = user.displayName
            emailUser.text = user.email
        }

        dayWeek(view)

        return view
    }

    @SuppressLint("SimpleDateFormat")
    private fun dayWeek(view: View) {

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        var calendar = Calendar.getInstance()
        val date = Date()
        calendar.time = date
        val current_week = calendar.get(Calendar.DAY_OF_WEEK)
        val starting_week = 1
        val last_week = 6

        val amount_startingDay: Int
        val amount_lastDay: Int

        val startingDay: Date
        val lastDay: Date

        if (current_week != 0) {
            amount_startingDay = starting_week - current_week + 1
            calendar.add(Calendar.DAY_OF_YEAR, amount_startingDay)
            val startingDayWeek = dateFormat.format(calendar.time)
            startingDay = dateFormat.parse(startingDayWeek)!!

            calendar = Calendar.getInstance()

            amount_lastDay = last_week + 2 - current_week
            calendar.add(Calendar.DAY_OF_YEAR, amount_lastDay)
            val lastDayWeek = dateFormat.format(calendar.time)
            lastDay = dateFormat.parse(lastDayWeek)!!

        } else {
            val today = Date()
            val lastDayWeek = dateFormat.format(today)
            lastDay = dateFormat.parse(lastDayWeek)!!

            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startingDayWeek = dateFormat.format(calendar.time)
            startingDay = dateFormat.parse(startingDayWeek)!!
        }

        calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val last = dateFormat.format(calendar.time)
        val lastDayMonth = dateFormat.parse(last)!!

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val start = dateFormat.format(calendar.time)
        val startDayMonth = dateFormat.parse(start)!!

        getTask(startingDay, lastDay, view, lastDayMonth, startDayMonth)

    }


    @Suppress("UNUSED_CHANGED_VALUE")
    @SuppressLint("SimpleDateFormat")
    private fun getTask(
        startingDay: Date,
        lastDay: Date,
        view: View,
        lastDayMonth: Date,
        startDayMonth: Date
    ) {
        val dataBase = DataBase()
        disposable = dataBase
            .retrieveData(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe { listTask ->
                checkedTask(view, listTask)
                progressWeek(view, startingDay, lastDay, listTask)
                var checkedCount = 0.0
                var total_task = 0.0
                for (task in listTask) {
                    val date = convertFormat(task.day)!!
                    if (date in startDayMonth..lastDayMonth ||
                            task.replay != "Нет >") {
                        total_task++
                        if (task.checked!!) {
                            checkedCount++
                        } else {
                            for (day in task.checkedTasks!!.keys) {
                                val dayFormat = day.replace('-', '.')
                                val date_checked = convertFormat(dayFormat)!!
                                if (date_checked in startDayMonth..lastDayMonth) {
                                    val checkedDate = day.replace('.', '-')
                                    if (task.checkedTasks!!.containsKey(checkedDate)) {
                                        val value = task.checkedTasks!![checkedDate]!!
                                        if (value) {
                                            checkedCount++
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                initializationMonth(view, checkedCount, total_task)
            }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun progressWeek(
        view: View,
        startingDay: Date,
        lastDay: Date,
        listTask: List<Task>?
    ) {
        val progress_week =
            view.findViewById<CircularProgressBar>(R.id.progress_made_week)
        val percent_week = view.findViewById<TextView>(R.id.percent_progress_made)
        var checkedCount = 0.0
        var total_task = 0.0
        for (task in listTask!!) {
            val date = convertFormat(task.day)!!
            if (date in startingDay..lastDay || task.replay != "Нет >")
            {
                total_task++
                if (task.checked!!) {
                    checkedCount++
                } else {
                    for (day in task.checkedTasks!!.keys) {
                        val dayFormat = day.replace('-', '.')
                        val formatedDate = convertFormat(dayFormat)!!
                        if (formatedDate in startingDay..lastDay) {
                            val checkedDate = day.replace('.', '-')
                            if (task.checkedTasks!!.containsKey(checkedDate)) {
                                val value = task.checkedTasks!![checkedDate]!!
                                if (value) {
                                    checkedCount++
                                }
                            }
                        }
                    }

                }
            }
        }
        if (checkedCount != 0.0 && total_task != 0.0) {
            val progress_current = (checkedCount / total_task) * 100
            percent_week.text = "${String.format("%.1f", progress_current)}%"
            progress_week.apply {
                setProgressWithAnimation(progress_current.toFloat(), 1000)
                progressMax = 100f
            }
        } else {
            progress_week.apply {
                setProgressWithAnimation(0F, 1000)
                progressMax = 100f
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertFormat(day: String): Date? {
        var date = day
        val arrayDate: List<String> = date.split(".")
        val month: String
        if (arrayDate[1].length == 1) {
            month = "0${arrayDate[1]}"
            date = "${arrayDate[0]}.$month.${arrayDate[2]}"
        }
        val dayTask:String
        if(arrayDate[0].length == 1){
            dayTask = "0${arrayDate[0]}"
            date = "$dayTask.${arrayDate[1]}.${arrayDate[2]}"
        }
        val format = SimpleDateFormat("dd.MM.yyy")
        val formatedDay = format.parse(date)
        return formatedDay
    }

    private fun checkedTask(view: View, listTask: List<Task>?) {
        val count_task = view.findViewById<TextView>(R.id.count_task)
        var count_checkedTask = 0
        for (task in listTask!!) {
            if (task.checked!!) {
                count_checkedTask++
            } else {
                if (task.checkedTasks!!.isNotEmpty()) {
                    count_checkedTask += task.checkedTasks!!.size
                }
            }
        }
        count_task.text = count_checkedTask.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun initializationMonth(
        view: View?,
        checkedCount: Double,
        totalTask: Double
    ) {

        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()

        val progress_month =
            view!!.findViewById<CircularProgressBar>(R.id.progress_made_month)
        val procent_month = view.findViewById<TextView>(R.id.percent_progressMade_month)
        if (checkedCount != 0.0 && totalTask != 0.0) {
            val progress_current = (checkedCount / totalTask) * 100
            procent_month.text = "${String.format("%.1f", progress_current)}%"
            progress_month.apply {
                setProgressWithAnimation(progress_current.toFloat(), 1000)
                progressMax = 100f
            }
        } else {
            progress_month.apply {
                setProgressWithAnimation(0F, 1000)
                progressMax = 100f
            }
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment =
                    GroupTask()
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

                val access: Fragment = Access()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, access)
                    .addToBackStack(null)
                    .commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment =
                    Progress()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, progress)
                    .addToBackStack(null)
                    .commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment =
                    Setting()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, setting)
                    .addToBackStack(null)
                    .commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment =
                    TechSupport()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, techSupport)
                    .addToBackStack(null)
                    .commit()

            }
            /*выход пользователя из системы*/
            R.id.signOut -> {
                disposable?.dispose()
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent =
                    Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        onDestroy()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mDrawerLayout!!.removeAllViews()
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }
    }

}