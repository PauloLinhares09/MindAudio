package com.packapps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.packapps.R
import com.packapps.model.audio_core.MediaPlayerApp
import com.packapps.model.dto.ItemAudioAux
import io.reactivex.subjects.PublishSubject

class FragmentListAudiosSeqAdapter : RecyclerView.Adapter<FragmentListAudiosSeqAdapter.MyFragHolder>() {
    var list = mutableListOf<ItemAudioAux>()

    private val subjectClick = PublishSubject.create<ItemAudioAux>()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFragHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_from_list, parent, false)

        return MyFragHolder(mView)
    }

    override fun onBindViewHolder(holder: MyFragHolder, position: Int) {
        val itemAux = list.get(position)
        itemAux.currentPosition = position
        val item = itemAux.itemAudio

        holder.tvTitle.text = item?.listName
        holder.tvDescription.text = item?.audioName
        holder.ibPlay.setOnClickListener {
            managerButtonPlay(holder, true)
            //Play

            subjectClick.onNext(itemAux)


        }

        itemAux.currentStatePlayback?.let { state ->

            when(state){
                MediaPlayerApp.MediaPlayerAppState.PAUSED -> {
                    holder.ibPlay.setImageResource(R.drawable.ic_play)
                    managerButtonPlay(holder, false)
                }
                MediaPlayerApp.MediaPlayerAppState.PLAYING -> {
                    holder.ibPlay.setImageResource(R.drawable.ic_pause)
                    managerButtonPlay(holder, false)
                }
                MediaPlayerApp.MediaPlayerAppState.BUFFERING -> {
                    managerButtonPlay(holder, true)
                }
                else ->{
                    holder.ibPlay.setImageResource(R.drawable.ic_play)
                    managerButtonPlay(holder, false)
                }

            }


        }?:kotlin.run {
            holder.ibPlay.setImageResource(R.drawable.ic_play)
            managerButtonPlay(holder, false)
        }


    }

    private fun managerButtonPlay(holder: MyFragHolder, showLoading: Boolean) {
        if (showLoading){
            holder.ibPlay.visibility = View.GONE
            holder.loading.visibility = View.VISIBLE
        }else{
            holder.ibPlay.visibility = View.VISIBLE
            holder.loading.visibility = View.GONE
        }

    }

    fun updateList(list: MutableList<ItemAudioAux>) {
        this.list = list

        notifyDataSetChanged()
    }

    fun updateJustItemOnPosition(itemAudio: ItemAudioAux, reset : Boolean = false){
        if (reset){
            for (i in 0..list.size -1){
                if (i != itemAudio.currentPosition)
                    list.get(i).currentStatePlayback = null
                else
                    list.get(i).currentStatePlayback = itemAudio.currentStatePlayback
            }

            notifyDataSetChanged()

            return
        }

        itemAudio.currentPosition?.let {currentPosition ->
            list.get(currentPosition).currentStatePlayback = itemAudio.currentStatePlayback

            notifyItemChanged(currentPosition)
        }

    }

    fun getSubjectClick() = subjectClick



    class MyFragHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val ibPlay = view.findViewById<ImageButton>(R.id.ibPlay)
        val loading = view.findViewById<FrameLayout>(R.id.loading)
    }
}