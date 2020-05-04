package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.R

class AdapterRecyclerView(val context: Context, data:ArrayList<String>):RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    val mData : ArrayList<String> = data

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val nameItem = itemView.findViewById<TextView>(R.id.nameItemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_list_subtask, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameItem.text = mData[position]

    }
}