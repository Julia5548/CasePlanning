package com.example.caseplanning.Sidebar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
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
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Setting.Setting
import com.example.caseplanning.mainWindow.WindowTask
import com.example.caseplanning.adapter.AdapterRecyclerViewAccess
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.disposables.Disposable

class Access(val accessUsers: HashMap<String, String>?) : Fragment(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var disposable: Disposable
    private lateinit var mDrawerLayout: DrawerLayout
    private var list_uid: ArrayList<String>? = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.provision_access, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar: ActionBar? = activity.supportActionBar
        actionBar!!.title = "Предоставление доступа"

        ButterKnife.bind(this, viewFragment)

        /*кнопка поиска*/
        val search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById(R.id.drawerLayout)
        /*подключение обработчика события кнопок бокового меню*/
        val navigationView = viewFragment.findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val navHeader = navigationView.getHeaderView(0)
        val emailUser = navHeader.findViewById<TextView>(R.id.emailText)
        val nameUser = navHeader.findViewById<TextView>(R.id.nameUser)


        val mToggle = ActionBarDrawerToggle(
            activity, mDrawerLayout, toolbar,
            R.string.Open, R.string.Close
        )

        mDrawerLayout.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()

        val user = FirebaseAuth.getInstance().currentUser!!
        user.let {
            nameUser.text = user.displayName
            emailUser.text = user.email
        }
        listUsers(viewFragment)

        if (accessUsers != null) {
            addAccessUsers(accessUsers, navigationView)
        }

        return viewFragment
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

    private fun listUsers(view: View) {

        val listUsers = view.findViewById<RecyclerView>(R.id.listViewUser)
        val layoutManager = LinearLayoutManager(context)

        val stringList = mutableMapOf<String, String>()
        val dataBaseTask = DataBase()
        var position = 1
        disposable = dataBaseTask
            .retrieveDataUser()
            .subscribe { user_data ->
                for ((uid, name) in user_data) {
                    if (uid != FirebaseAuth.getInstance().currentUser!!.uid) {
                        stringList[uid] = name
                    }
                    if (position == user_data.size) {
                        listUsers.layoutManager = layoutManager
                        listUsers.adapter =
                            AdapterRecyclerViewAccess(context!!, stringList)
                        if (!disposable.isDisposed)
                            disposable.dispose()
                    }
                    position++
                }
            }
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
                val progress: Fragment = Progress(accessUsers)
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
                val techSupport: Fragment = TechSupport(accessUsers)
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
                startActivity(intent)
            }
            else -> {
                for ((uid, name) in accessUsers!!) {
                    if (menuItem.title == name) {
                        val windowTask: Fragment =
                            WindowTask()
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.linerLayout, windowTask).addToBackStack(null).commit()
                    }
                }
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        onDestroy()
        return true
    }

    override fun onPause() {
        super.onPause()
        list_uid = null
    }

    override fun onDestroy() {
        super.onDestroy()
        list_uid = null
        mDrawerLayout.removeAllViews()
        if (!disposable.isDisposed)
            disposable.dispose()
    }
}