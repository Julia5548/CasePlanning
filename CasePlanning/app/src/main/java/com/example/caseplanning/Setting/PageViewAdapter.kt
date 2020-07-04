package com.example.caseplanning.Setting

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.caseplanning.DataBase.Users

class PageViewAdapter : FragmentPagerAdapter {

    private var mList_access_users: HashMap<String, Users>? = null
    private var mList_users_shared: HashMap<String, Users>? = null

    constructor(
        fragmentManager: FragmentManager,
        list_access_users: HashMap<String, Users>?,
        list_users_shared: HashMap<String, Users>?
    ):super(fragmentManager){
        mList_access_users = list_access_users
        mList_users_shared = list_users_shared
    }

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null

        when(position){
            0 -> fragment = UsersAccess(mList_access_users)
            1 -> fragment = UsersShared(mList_users_shared)
        }
        return fragment!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        mList_access_users = null
        mList_users_shared = null
        super.destroyItem(container, position, `object`)
    }

    override fun getCount(): Int = 2
}