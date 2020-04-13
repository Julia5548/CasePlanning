package com.example.caseplanning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caseplanning.R
import kotlinx.android.synthetic.main.card_list.view.*

class AdapterRecyclerViewFolder(val context: Context, data:ArrayList<String>):RecyclerView.Adapter<AdapterRecyclerViewFolder.ViewHolder>() {

    val mData : ArrayList<String> = data

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val dataFolder = itemView.findViewById<TextView>(R.id.nameItemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.card_list, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.dataFolder.text = mData[position]

    }
}