package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.R
import com.example.caseplanning.Setting.AccessSetting
import com.example.caseplanning.Setting.Account
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.HashMap

class AdapterViewSetting(
    val context: Context,
    data: HashMap<String, String>,
    val name: String?,
    val email: String?,
    shared_user: HashMap<String, String>
) : RecyclerView.Adapter<AdapterViewSetting.ViewHolder>() {

    val mData: HashMap<String, String> = data
    var mSharedUsers: HashMap<String, String> = shared_user

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name_title = itemView.findViewById<TextView>(R.id.title)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.card_view)
        val name_subText = itemView.findViewById<TextView>(R.id.sub_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_setting, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size


    override fun onBindViewHolder(holder: AdapterViewSetting.ViewHolder, position: Int) {
        holder.name_title.text = mData.keys.elementAt(position)
        holder.name_subText.text = mData.values.elementAt(position)

        holder.cardView.setOnClickListener {
            if (holder.position == 0) {
                users()
            } else {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.linerLayout, Account(name!!, email!!))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun update(shared_user: HashMap<String, String>) {
        mSharedUsers.clear()
        this.mSharedUsers.putAll(shared_user)
        notifyDataSetChanged()
    }

    private fun users(){
        val dataBase = DataBase()
        var disposable: Disposable? = null
        disposable = dataBase
            .readUser()
            .subscribe {users->
                val list_uid = hashMapOf<String,Users>()
                for((key, name) in users) {
                    val user = Users(name = name)
                    list_uid[key] = user
                }
                listUsers(list_uid, disposable)
            }
    }
    private fun listUsers(list_users: HashMap<String, Users>, disposable: Disposable?) {

        if(disposable != null && !disposable.isDisposed)
            disposable.dispose()

        val users_shared = hashMapOf<String, Users>()
        val users_access = hashMapOf<String, Users>()
        val dataBase = DataBase()
        if (list_users.isNotEmpty()) {
            var position = 1
            for ((uid, user) in list_users) {
                val mDisposable = dataBase
                    .retrieveAccess(uid)
                    .subscribe { accessUsers ->
                        if (accessUsers.keys.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            user.accessUsers = accessUsers
                            users_access[uid] = user
                        }
                        if(mSharedUsers.contains(uid)){
                            user.accessUsers = accessUsers
                            users_shared[uid] = user
                        }
                        if(position == list_users.size) {
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                                .replace(R.id.linerLayout, AccessSetting(users_access, users_shared))
                                .addToBackStack(null)
                                .commit()
                        }
                        position++
                    }
            }
        }
    }
}