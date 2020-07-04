package com.example.caseplanning.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.DataBase.DataBase
import com.example.caseplanning.DataBase.Users
import com.example.caseplanning.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

class AdapterSharedAccessUsers(
    val context: Context,
    data: HashMap<String, Users>,
    val tag: String,
    val userTextAccess: TextView,
    val refuseAccess: TextView
) : RecyclerView.Adapter<AdapterSharedAccessUsers.ViewHolder>() {

    var mData: HashMap<String, Users> = data

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name_user = itemView.findViewById<TextView>(R.id.name_user)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.card_view_user)
        val switch = itemView.findViewById<SwitchMaterial>(R.id.switch_refuse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_shared_users, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name_user.text = mData.values.elementAt(position).name
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                var message = ""
                if (tag == "user_shared") {
                    message =
                        "Вы действительно хотите отказаться от представленного доступа к задачам?"
                } else {
                    message = "Вы действительно хотите отменить доступ к вашим задачам"
                }
                holder.switch.text = "Отказано"
                holder.switch.setTextColor(context.resources.getColor(R.color.red))
                holder.switch.thumbDrawable.setColorFilter(
                    Color.argb(81, 255, 17, 0),
                    PorterDuff.Mode.MULTIPLY
                )
                holder.switch.trackDrawable.setColorFilter(
                    Color.argb(60, 255, 17, 0),
                    PorterDuff.Mode.MULTIPLY
                )
                MaterialAlertDialogBuilder(context)
                    .setTitle("Отказ в доступе")
                    .setMessage(message)
                    .setPositiveButton("Да") { dialog, which ->
                        dialog.dismiss()
                        val dataBase = DataBase()
                        val accessUsers = hashMapOf<String, String>()
                        val user = FirebaseAuth.getInstance().currentUser!!
                        if (tag == "user_shared") {
                            mData.remove(mData.keys.elementAt(position))
                            for ((key, userdata) in mData)
                                accessUsers[key] = userdata.name!!
                            dataBase.updateAccessUsers(accessUsers, user.uid)
                            updateDate(mData)
                        } else {
                            mData.values.elementAt(position).accessUsers.remove(user.uid)
                            for ( (key, value) in mData.values.elementAt(position).accessUsers)
                                accessUsers[key] = value
                            dataBase.updateAccessUsers(accessUsers, mData.keys.elementAt(position))
                            mData.remove(mData.keys.elementAt(position))
                            updateDate(mData)
                        }
                        try {
                            notifyItemChanged(position)
                        } catch (e: Exception) {
                            Log.d("TAG", e.toString())
                        }
                    }
                    .setNegativeButton("Отмена") { dialog, which ->
                        dialog.dismiss()
                        holder.switch.text = "Предоставлено"
                        holder.switch.isChecked = true
                        holder.switch.setTextColor(context.resources.getColor(R.color.green))
                    }
                    .show()
            }
        }
    }

    fun updateDate(mData: HashMap<String, Users >) {
        this.mData = hashMapOf()
        this.mData.putAll(mData)
        if (this.mData.isEmpty()) {
            userTextAccess.visibility = View.VISIBLE
            refuseAccess.visibility = View.GONE
        }
        notifyDataSetChanged()
    }
}