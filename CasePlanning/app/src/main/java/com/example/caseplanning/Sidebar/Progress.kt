package com.example.caseplanning.Sidebar

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
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Setting.Setting
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.disposables.Disposable

class Progress: Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mDrawerLayout: DrawerLayout? = null
    lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.progress_made, container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbarGroup)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Группы задач"

        ButterKnife.bind(this, viewFragment)

        /*кнопка поиска*/
        val search = viewFragment.findViewById<MaterialSearchView>(R.id.search)
        search.closeSearch()

        /*боковое меню*/
        mDrawerLayout = viewFragment.findViewById<DrawerLayout>(R.id.drawerLayout)
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

        mDrawerLayout!!.addDrawerListener(mToggle)
        /*проверяем состояние*/
        mToggle.syncState()

        val user = FirebaseAuth.getInstance().currentUser!!
        user.let {
            nameUser.text = user.displayName
            emailUser.text = user.email
        }
        return viewFragment
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
                disposable.dispose()
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

}