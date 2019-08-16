package com.packapps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.packapps.dto.ItemAudio
import com.packapps.R
import com.packapps.audio_core.MediaPlayerApp
import io.reactivex.subjects.PublishSubject

class FragmentListAudiosSeqAdapter : RecyclerView.Adapter<FragmentListAudiosSeqAdapter.MyFragHolder>() {
    var list = mutableListOf<ItemAudio>()

    private val subjectClick = PublishSubject.create<ItemAudio>()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFragHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_from_list, parent, false)

        return MyFragHolder(mView)
    }

    override fun onBindViewHolder(holder: MyFragHolder, position: Int) {
        val item = list.get(position)
        item.currentPosition = position

        holder.tvTitle.text = item.listName
        holder.tvDescription.text = item.audioName
        holder.ibPlay.setOnClickListener {
            //Play
            subjectClick.onNext(item)
        }

        item.stateMediaPlayer?.let { state ->

            when(state){
                MediaPlayerApp.MediaPlayerAppState.PAUSED -> {
                    holder.ibPlay.setImageResource(R.drawable.ic_play)
                }
                MediaPlayerApp.MediaPlayerAppState.PLAYING -> {
                    holder.ibPlay.setImageResource(R.drawable.ic_pause)
                }
                MediaPlayerApp.MediaPlayerAppState.BUFFERING -> {
                    holder.ibPlay.setImageResource(android.R.drawable.stat_sys_upload)
                }
                else ->{
                    holder.ibPlay.setImageResource(R.drawable.ic_play)
                }

            }


        }?:kotlin.run {
            holder.ibPlay.setImageResource(R.drawable.ic_play)
        }


    }

    fun updateList(list: MutableList<ItemAudio>) {
        this.list = list

        notifyDataSetChanged()
    }

    fun updateJustItemOnPosition(itemAudio: ItemAudio){
        itemAudio.currentPosition?.let {currentPosition ->
            list.get(currentPosition).stateMediaPlayer = itemAudio.stateMediaPlayer

            notifyItemChanged(currentPosition)
        }
    }

    fun getSubjectClick() = subjectClick


    class MyFragHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val ibPlay = view.findViewById<ImageButton>(R.id.ibPlay)
    }
}