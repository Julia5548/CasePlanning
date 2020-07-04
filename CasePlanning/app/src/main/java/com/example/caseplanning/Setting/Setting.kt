package com.example.caseplanning.Setting

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
import androidx.core.view.get
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Sidebar.Access
import com.example.caseplanning.Sidebar.Progress
import com.example.caseplanning.Sidebar.TechSupport
import com.example.caseplanning.adapter.AdapterViewSetting
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable

class Setting(val accessUsers: HashMap<String, String>?) : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mDrawerLayout: DrawerLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.setting, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Настройки"

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
        val dataBase = DataBase()
        var disposable:Disposable? = null
        disposable = dataBase
            .retrieveAccess(user.uid)
            .subscribe { users->
                listSetting(view, user.displayName, user.email, users, disposable)
            }

        if (accessUsers != null) {
            addAccessUsers(accessUsers, navigationView)
        }

        return view
    }

    private fun addAccessUsers(
        accessUsers: HashMap<String, String>,
        navigationView: NavigationView?
    ) {
        val menu = navigationView!!.menu
        if (menu.size >= 7) {
            for (position in 6 until menu.size())
                menu[position].subMenu.clear()
        }
        val subMenu = menu.addSubMenu("Пользователи")
        if (accessUsers.isNotEmpty()) {
            for ((uid, user) in accessUsers) {
                subMenu.add(0, 6, 0, user)
                navigationView.invalidate()
            }
        }
    }


    private fun listSetting(
        view: View,
        name: String?,
        email: String?,
        shared_user: HashMap<String, String>,
        disposable: Disposable?
    ) {
        val listSetting = view.findViewById<RecyclerView>(R.id.list_setting)
        listSetting.layoutManager = LinearLayoutManager(context)
        val setting_list = HashMap<String, String>()
        setting_list["Доступ"] = "Управление доступом"
        setting_list["Аккаунт"] = "Управление аккаунтом"
        val mAdapterSetting = AdapterViewSetting(context!!, setting_list, name, email, shared_user)

        if (shared_user.isNullOrEmpty())
            mAdapterSetting.update(shared_user)

        listSetting.adapter = mAdapterSetting


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            /*группа задач*/
            R.id.groupTask -> {

                val groupTask: Fragment =
                    GroupTask(accessUsers)
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

                val access: Fragment = Access(accessUsers)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, access)
                    .addToBackStack(null)
                    .commit()

            }
            /*прогресс выполнения задач*/
            R.id.progress -> {

                val progress: Fragment =
                    Progress(accessUsers)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, progress)
                    .addToBackStack(null)
                    .commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment =
                    Setting(accessUsers)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, setting)
                    .addToBackStack(null)
                    .commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment =
                    TechSupport(accessUsers)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.linerLayout, techSupport)
                    .addToBackStack(null)
                    .commit()

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
                for ((uid, name) in accessUsers!!) {
                    if (menuItem.title == name){
                        val windowTask: Fragment =
                            WindowTask()
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.linerLayout, windowTask).addToBackStack(null).commit()
                    }
                }
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        onDestroy()
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        mDrawerLayout!!.removeAllViews()

    }
}