package com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dto.ItemAudio
import com.packapps.R

class FragmentListAudiosSeqAdapter : RecyclerView.Adapter<FragmentListAudiosSeqAdapter.MyFragHolder>() {
    var list = mutableListOf<ItemAudio>()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFragHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_from_list, parent, false)

        return MyFragHolder(mView)
    }

    override fun onBindViewHolder(holder: MyFragHolder, position: Int) {
        val item = list.get(position)

        holder.tvTitle.text = item.listName
        holder.tvDescription.text = item.audioName
    }

    fun updateList(list: MutableList<ItemAudio>) {
        this.list = list

        notifyDataSetChanged()
    }


    class MyFragHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
    }
}