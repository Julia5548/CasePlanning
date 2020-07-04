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
    users_access: HashMap<String, Users>,
    users_shared: HashMap<String, Users>
) : Fragment() {

    private var viewPager: ViewPager? = null
    private var user_shared: TextView? = null
    private var user_access: TextView? = null
    private var pageViewAdapter: PageViewAdapter? = null
    private var list_access_users: HashMap<String, Users>? = users_access
    private var list_users_shared: HashMap<String, Users>? = users_shared

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

    override fun onDestroy() {
        super.onDestroy()
        viewPager = null
        user_shared = null
        user_access = null
        pageViewAdapter = null
        list_access_users = null
        list_users_shared = null

    }
}