package com.packapps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.packapps.model.dto.CardTab
import com.packapps.model.dto.TypeCardTab
import com.packapps.R

class MainCardAdapter : RecyclerView.Adapter<MainCardAdapter.MyMainHolder>(){
    var listCardsTab = mutableListOf<CardTab>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMainHolder {
        var view = View(parent.context)
        when(viewType){
            TypeCardTab.MAIN_LIST_EMPTY.code ->
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_main, parent, false)
            TypeCardTab.MAIN_LIST.code ->
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_container_to_fragment, parent, false)
        }


        return MyMainHolder(view)
    }

    override fun getItemCount(): Int  =  listCardsTab.size

    override fun onBindViewHolder(holder: MyMainHolder, position: Int) {
        val viewType = listCardsTab.get(position).typeCardTab
        when(viewType) {
            TypeCardTab.MAIN_LIST_EMPTY.code ->
                bindCardListEmpty(holder)
            TypeCardTab.MAIN_LIST.code ->
                bindFragmentCardList(holder)
        }
    }

    private fun bindFragmentCardList(holder: MyMainHolder) {

    }

    private fun bindCardListEmpty(holder: MyMainHolder) {

    }

    override fun getItemViewType(position: Int): Int {
        return listCardsTab.get(position).typeCardTab
    }


    class MyMainHolder(view : View) : RecyclerView.ViewHolder(view) {
        //For card list empty
        val imageMain = view.findViewById<ImageView>(R.id.ivMain)
        val imageIconExtra = view.findViewById<ImageView>(R.id.ivIconExtra)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescripton = view.findViewById<TextView>(R.id.tvDescription)
        val ibMainAction = view.findViewById<ImageButton>(R.id.ibMainAction)

        //Fragment Card container
        val container = view.findViewById<FrameLayout>(R.id.container)


    }


    fun updateList(list : MutableList<CardTab>){
        this.listCardsTab = list

        notifyDataSetChanged()
    }

}