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

class UsersAccess(listAccessUsers: HashMap<String, Users>?) : Fragment() {

    var mListUsersAccess = listAccessUsers
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
        val view = inflater.inflate(R.layout.user_access, container, false)

        if (mListUsersAccess != null && mListUsersAccess!!.isNotEmpty()) {
            val user_text_access = view.findViewById<TextView>(R.id.user_text_access)
            val refuse_access = view.findViewById<TextView>(R.id.refuse_access)
            val recycler_access_users = view.findViewById<RecyclerView>(R.id.recycler_access_user)

            ButterKnife.bind(this, view)
            user_text_access.visibility = View.GONE
            refuse_access.visibility = View.VISIBLE
            recycler_access_users.visibility = View.VISIBLE

            recycler_access_users.layoutManager = LinearLayoutManager(context)

            mAdapter = AdapterSharedAccessUsers(
                context!!, mListUsersAccess!!, "user_access",
                user_text_access,
                refuse_access
            )

            recycler_access_users.adapter = mAdapter
        }
        return view
    }

    @OnClick(R.id.refuse_access)
    fun refuseAccess() {
        val dataBase = DataBase()
        MaterialAlertDialogBuilder(context)
            .setTitle("Отказ в доступе")
            .setMessage("Вы действительно хотите отказать всем пользователям в доступе к вашим задачам?")
            .setPositiveButton("Да") { dialog, which ->
                dialog.dismiss()
                val update_user = Users()
                val user = FirebaseAuth.getInstance().currentUser!!
                for ((key, value) in mListUsersAccess!!) {
                    value.accessUsers.keys.remove(user.uid)
                    update_user.accessUsers = value.accessUsers
                    dataBase.updateAccessUsers(update_user.accessUsers, key)
                }
                try {
                    mListUsersAccess = hashMapOf()
                    mAdapter.updateDate(mListUsersAccess!!)
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