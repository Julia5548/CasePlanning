package com.example.caseplanning.Setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterSharedAccessUsers

class UsersAccess(listAccessUsers: MutableMap<String, Users>?) : Fragment() {

    val mListUsersAccess = listAccessUsers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_access, container, false)

        if(mListUsersAccess != null && mListUsersAccess.isNotEmpty()) {
            val user_text_access = view.findViewById<TextView>(R.id.user_text_access)
            val refuse_access= view.findViewById<TextView>(R.id.refuse_access)
            val recycler_access_users = view.findViewById<RecyclerView>(R.id.recycler_access_user)

            user_text_access.visibility = View.GONE
            refuse_access.visibility = View.VISIBLE
            recycler_access_users.visibility = View.VISIBLE

            recycler_access_users.layoutManager  = LinearLayoutManager(context)
            recycler_access_users.adapter = AdapterSharedAccessUsers(context!!, mListUsersAccess,"user_access")
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}