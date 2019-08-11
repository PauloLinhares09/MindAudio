package com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dto.ItemOption
import com.packapps.R

class MainCardOptionsAdapter : RecyclerView.Adapter<MainCardOptionsAdapter.MyOptionsHolder>(){

    var list = mutableListOf<ItemOption>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOptionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_botton_option, parent, false)

        return MyOptionsHolder(view)
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: MyOptionsHolder, position: Int) {
        val item = list.get(position)
        holder.tvTitle.text = item.title
        holder.ibIcon.setImageResource(item.icon)
    }

    fun updateList(list: MutableList<ItemOption>) {
        this.list = list
        notifyDataSetChanged()
    }

    class MyOptionsHolder(view : View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val ibIcon = view.findViewById<ImageButton>(R.id.ibIconOption)
    }


}