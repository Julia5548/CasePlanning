package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.R

class AdapterRecyclerViewAccess(val context : Context, data:ArrayList<String>) : RecyclerView.Adapter<AdapterRecyclerViewAccess.ViewHolder>() {

    val mData: ArrayList<String> = data

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val dataUser : TextView = itemView.findViewById(R.id.nameItemList)
        val buttonAccess : Button = itemView.findViewById(R.id.buttonAccess)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.provision_access_button, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataUser.text = mData[position]
        holder.buttonAccess.setOnClickListener{
            view->
            Toast.makeText(context, "Доступ разрешен", Toast.LENGTH_LONG).show()
        }
    }
}