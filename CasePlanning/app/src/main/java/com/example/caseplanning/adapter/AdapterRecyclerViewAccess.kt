package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable

class AdapterRecyclerViewAccess(
    val context: Context,
    data: MutableMap<String, Users>
) :
    RecyclerView.Adapter<AdapterRecyclerViewAccess.ViewHolder>() {

    val mData: MutableMap<String, Users> = data
    private var mAccessUsers: ArrayList<String>? = arrayListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameUser: TextView = itemView.findViewById(R.id.nameUser)
        val access: TextView = itemView.findViewById(R.id.access)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.access_card, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameUser.text = mData.values.elementAt(position).name

        holder.access.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Разрешить?")
                .setMessage("Действительно ли разрешить доступ пользователю ${mData.values.elementAt(position).name}")
                .setPositiveButton("Разрешить") { dialog, which ->
                    dialog.dismiss()
                    //другим пользователям
                    val mCurrentUid = FirebaseAuth.getInstance().currentUser!!.uid
                    if (!mData.values.elementAt(position).accessUsers.contains(mCurrentUid)) {
                        mData.values.elementAt(position).accessUsers.add(mCurrentUid)
                        val user = mData.values.elementAt(position)
                        val dataBase = DataBase()
                        dataBase.updateDataUser(user, mData.keys.elementAt(position))
                        Toast.makeText(context, "Доступ разрешен", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        Toast.makeText(context, "Данному пользователю уже был разрешен доступ", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}