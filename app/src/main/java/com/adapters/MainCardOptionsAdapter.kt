package com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.packapps.R

class MainCardOptionsAdapter : RecyclerView.Adapter<MainCardOptionsAdapter.MyOptionsHolder>(){

    var list = mutableListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOptionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_botton_option, parent, false)

        return MyOptionsHolder(view)
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: MyOptionsHolder, position: Int) {

    }

    fun updateList(list: MutableList<String>) {
        this.list = list
        notifyDataSetChanged()
    }
    
    class MyOptionsHolder(view : View) : RecyclerView.ViewHolder(view) {

    }


}