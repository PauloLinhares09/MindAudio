package com.packapps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.packapps.model.dto.ItemOption
import com.packapps.R
import io.reactivex.subjects.PublishSubject

class MainCardOptionsAdapter(val publishSubject: PublishSubject<Int>) : RecyclerView.Adapter<MainCardOptionsAdapter.MyOptionsHolder>(){

    var list = mutableListOf<ItemOption>()

    companion object{
        const val MEDIA_CREATE = 0
        const val MEDIA_DELETE = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOptionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_botton_option, parent, false)

        return MyOptionsHolder(view)
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: MyOptionsHolder, position: Int) {
        val item = list.get(position)
        holder.tvTitle.text = item.title
        holder.ibIcon.setImageResource(item.icon)
        //click item
        holder.itemView.setOnClickListener {
            publishSubject.onNext(position) //MEDIA_CREATE or MEDIA_DELETE / 0 or 1
        }
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