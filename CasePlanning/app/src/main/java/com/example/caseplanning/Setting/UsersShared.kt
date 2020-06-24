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

class UsersShared(listUsersShared: MutableMap<String, Users>?) : Fragment() {

    private var mListUsersShared = listUsersShared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_shared, container, false)

        if(mListUsersShared != null && mListUsersShared!!.isNotEmpty()) {
            val user_text_shared = view.findViewById<TextView>(R.id.user_text_shared)
            val refuse_access= view.findViewById<TextView>(R.id.refuse_access)
            val recycler_shared_users = view.findViewById<RecyclerView>(R.id.recycler_shared_user)

            user_text_shared.visibility = View.GONE
            refuse_access.visibility = View.VISIBLE
            recycler_shared_users.visibility = View.VISIBLE

            recycler_shared_users.layoutManager  = LinearLayoutManager(context)
            recycler_shared_users.adapter = AdapterSharedAccessUsers(context!!, mListUsersShared!!,"user_shared")
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}