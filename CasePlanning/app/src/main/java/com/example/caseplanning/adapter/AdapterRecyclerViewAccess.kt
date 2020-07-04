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
    data: MutableMap<String, String>
) :
    RecyclerView.Adapter<AdapterRecyclerViewAccess.ViewHolder>() {

    val mData: MutableMap<String, String> = data
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

        holder.nameUser.text = mData.values.elementAt(position)

        holder.access.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Разрешить?")
                .setMessage(
                    "Действительно ли разрешить доступ пользователю ${mData.values.elementAt(
                        position
                    )}"
                )
                .setPositiveButton("Разрешить") { dialog, which ->
                    dialog.dismiss()
                    //другим пользователям
                    val user = FirebaseAuth.getInstance().currentUser!!
                    var disposable : Disposable? = null
                    val dataBase = DataBase()
                    disposable = dataBase
                        .retrieveAccess(mData.keys.elementAt(position))
                        .subscribe {
                            if (!it.contains(user.uid)) {
                                it[user.uid] = user.displayName!!
                                dataBase.updateAccessUsers(it, mData.keys.elementAt(position))
                                Toast.makeText(context, "Доступ разрешен", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Данному пользователю уже был разрешен доступ",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            if(disposable != null && !disposable!!.isDisposed)
                                disposable!!.dispose()
                        }
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}