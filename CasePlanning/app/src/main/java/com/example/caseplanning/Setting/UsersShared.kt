package com.example.caseplanning.Setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.R
import com.example.caseplanning.adapter.AdapterSharedAccessUsers
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class UsersShared(listUsersShared: MutableMap<String, Users>?) : Fragment() {

    private var mListUsersShared = listUsersShared
    private lateinit var mAdapter: AdapterSharedAccessUsers

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

        ButterKnife.bind(this, view)
        if (mListUsersShared != null && mListUsersShared!!.isNotEmpty()) {
            val user_text_shared = view.findViewById<TextView>(R.id.user_text_shared)
            val refuse_access = view.findViewById<TextView>(R.id.refuse_access)
            val recycler_shared_users = view.findViewById<RecyclerView>(R.id.recycler_shared_user)

            user_text_shared.visibility = View.GONE
            refuse_access.visibility = View.VISIBLE
            recycler_shared_users.visibility = View.VISIBLE

            recycler_shared_users.layoutManager = LinearLayoutManager(context)
            mAdapter = AdapterSharedAccessUsers(context!!, mListUsersShared!!, "user_shared", user_text_shared,
                refuse_access)
            recycler_shared_users.adapter = mAdapter
        }

        return view
    }

    @OnClick(R.id.refuse_access)
    fun refuseAccess() {
        val dataBase = DataBase()
        MaterialAlertDialogBuilder(context)
            .setTitle("Отказ в доступе")
            .setMessage("Вы действительно хотите отказаться от всех предоставленных вам доступов?")
            .setPositiveButton("Да") { dialog, which ->
                dialog.dismiss()
                val update_user = Users()
                val user = FirebaseAuth.getInstance().currentUser!!
                user.let {
                    update_user.name = user.displayName
                    update_user.email = user.email
                }
                mListUsersShared = mutableMapOf()
                dataBase.updateDataUser(update_user, user.uid)
                try {
                    mAdapter.updateDate(mListUsersShared!!)
                } catch (e: Exception) {
                    Log.d("TAG", e.toString())
                }
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}