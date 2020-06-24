package com.example.caseplanning.Setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.GroupTask.GroupTask
import com.example.caseplanning.MainActivity
import com.example.caseplanning.R
import com.example.caseplanning.Sidebar.Access
import com.example.caseplanning.Sidebar.Progress
import com.example.caseplanning.Sidebar.TechSupport
import com.example.caseplanning.mainWindow.WindowTask
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class AccessSetting(
    users_access: MutableMap<String, Users>,
    users_shared: MutableMap<String, Users>
) : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var viewPager: ViewPager? = null
    private var user_shared: TextView? = null
    private var user_access: TextView? = null
    private var pageViewAdapter: PageViewAdapter? = null
    var mDrawerLayout: DrawerLayout? = null
    private var list_access_users: MutableMap<String, Users>? = users_access
    private var list_users_shared: MutableMap<String, Users>? = users_shared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.access_setting, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = "Доступ"
        toolbar.setTitleTextColor(resources.getColor(R.color.textWhite))

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
        user_access = view.findViewById(R.id.user_access)
        user_shared = view.findViewById(R.id.user_shared)
        viewPager = view.findViewById(R.id.view_pager)

        pageViewAdapter =
            PageViewAdapter(childFragmentManager, list_access_users, list_users_shared)

        if (viewPager != null) {
            viewPager!!.adapter = pageViewAdapter
        }
        pageViewAdapter!!.notifyDataSetChanged()
        viewPager!!.currentItem = 0

        user_access!!.textSize = 18f
        user_access!!.setTextColor(context!!.getColor(R.color.colorElementTwoPageView))

        user_shared!!.textSize = 15f
        user_shared!!.setTextColor(context!!.getColor(R.color.colorElementPageView))

        user_access!!.setOnClickListener {
            viewPager?.currentItem = 0
        }

        user_shared!!.setOnClickListener {
            viewPager?.currentItem = 1
        }
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onPageSelected(position: Int) {
                onChangeTab(position)
            }
        })

        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun onChangeTab(position: Int) {
        if (position == 0) {
            user_access!!.textSize = 18f
            user_access!!.setTextColor(context!!.getColor(R.color.colorElementTwoPageView))

            user_shared!!.textSize = 15f
            user_shared!!.setTextColor(context!!.getColor(R.color.colorElementPageView))
        }
        if (position == 1) {
            user_access!!.textSize = 15f
            user_access!!.setTextColor(context!!.getColor(R.color.colorElementPageView))

            user_shared!!.textSize = 18f
            user_shared!!.setTextColor(context!!.getColor(R.color.colorElementTwoPageView))
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
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
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
        viewPager = null
        user_shared = null
        user_access = null
        pageViewAdapter = null
        list_access_users = null
        list_users_shared = null

    }
}