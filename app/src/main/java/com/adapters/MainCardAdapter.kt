package com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dto.CardTab
import com.dto.TypeCardTab
import com.packapps.R

class MainCardAdapter : RecyclerView.Adapter<MainCardAdapter.MyMainHolder>(){
    var listCardsTab = mutableListOf<CardTab>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMainHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_main, parent, false)

        return MyMainHolder(view)
    }

    override fun getItemCount(): Int  =  listCardsTab.size

    override fun onBindViewHolder(holder: MyMainHolder, position: Int) {
//        if (holder.itemViewType == TypeCardTab.MAIN_LIST_EMPTY.code)
            bindCardListEmpty(holder)
    }

    private fun bindCardListEmpty(holder: MyMainHolder) {

    }


    class MyMainHolder(view : View) : RecyclerView.ViewHolder(view) {
        //For card list empty
        val imageMain = view.findViewById<ImageView>(R.id.ivMain)
        val imageIconExtra = view.findViewById<ImageView>(R.id.ivIconExtra)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescripton = view.findViewById<TextView>(R.id.tvDescription)
        val ibMainAction = view.findViewById<ImageButton>(R.id.ibMainAction)

    }


    fun updateList(list : MutableList<CardTab>){
        this.listCardsTab = list

        notifyDataSetChanged()
    }

}