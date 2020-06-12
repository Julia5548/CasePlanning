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
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable

class TechSupport: Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mDrawerLayout: DrawerLayout? = null
    lateinit var disposable: Disposable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewFragment = inflater.inflate(R.layout.tech_support , container, false)

        val toolbar = viewFragment.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Тех. поддержка"

        ButterKnife.bind(this, viewFragment)

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

        val dataBaseTask = DataBase()
        disposable = dataBaseTask
            .retrieveDataUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribe({ user ->
                nameUser.text = user.name
                emailUser.text = user.email
            },
                { throwable ->
                    throwable.printStackTrace()
                })

        return viewFragment
    }

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

                val progress: Fragment =
                    Progress()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, progress)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*настройки*/
            R.id.setting -> {

                val setting: Fragment =
                    Setting()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, setting)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            /*техподдержка*/
            R.id.techSupport -> {

                val techSupport: Fragment =
                    TechSupport()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.linerLayout, techSupport)
                transaction.addToBackStack(null)
                transaction.commit()

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