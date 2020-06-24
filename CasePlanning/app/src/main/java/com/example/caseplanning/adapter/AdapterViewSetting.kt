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

class AdapterViewSetting(
    val context: Context,
    data: HashMap<String, String>,
    name: String?,
    email: String?,
    shared_user: ArrayList<String>
) : RecyclerView.Adapter<AdapterViewSetting.ViewHolder>() {

    val mData: HashMap<String, String> = data
    val mName: String = name!!
    val mEmail: String = email!!
    var mSharedUsers: ArrayList<String> = shared_user
    var list_uid: ArrayList<String>? = null

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
                createListUid()
            } else {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.linerLayout, Account(mName, mEmail, mSharedUsers))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getListUid(): ArrayList<String>? = list_uid

    private fun createListUid() {
        val dataBaseTask = DataBase()
        var disposable: Disposable? = null
        list_uid = arrayListOf()
        disposable = dataBaseTask
            .retrieveDataUid()
            .subscribe { uids ->
                if (list_uid != null) {
                    if (list_uid!!.size > 0) {
                        list_uid = arrayListOf()
                    }
                    for (uid_user in uids)
                        list_uid!!.add(uid_user.id!!)
                    listUsers(disposable)
                }
            }
    }

    private fun listUsers(disposable: Disposable?) {

        var mDisposable = disposable
        val users_shared = mutableMapOf<String, Users>()
        val users_acces = mutableMapOf<String, Users>()
        val dataBaseTask = DataBase()
        val uids = getListUid()
        if (uids != null) {
            var position = 1
            for (uid in uids) {
                mDisposable = dataBaseTask
                    .retrieveDataUser(uid)
                    .subscribe { user_data ->
                        if (user_data.accessUsers.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            users_acces[uid] = user_data
                        }
                        if(mSharedUsers.contains(uid)){
                            users_shared[uid] = user_data
                        }
                        if(position == uids.size) {
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                                .replace(R.id.linerLayout, AccessSetting(users_acces, users_shared))
                                .addToBackStack(null)
                                .commit()
                            mSharedUsers = arrayListOf()

                            if (mDisposable != null && !mDisposable!!.isDisposed)
                                mDisposable!!.dispose()
                        }
                        position++
                    }
            }
        }
    }
}