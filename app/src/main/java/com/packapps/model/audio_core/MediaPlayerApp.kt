package com.packapps.model.audio_core

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.packapps.model.utils.LogApp
import com.packapps.repository.entity.ItemAudio
import io.reactivex.subjects.PublishSubject


class MediaPlayerApp(
    val androidContext: Context,
    val subject: PublishSubject<Boolean>
) : MediaPlayer.OnCompletionListener {

    private var itemAudio: ItemAudio? = null

    override fun onCompletion(mp: MediaPlayer?) {
        LogApp.i("MediaPlayer", "Item audio end")
        subject.onNext(true)
    }


    var mMediaPlayer : MediaPlayer? = null

    private fun initializePlayer(){
        releasePlayer()
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setOnCompletionListener(this)
    }

    fun loadMedia(itemAudio: ItemAudio){
        initializePlayer()
        this.itemAudio = itemAudio
        val path = itemAudio.audioPath
        val uriAux = Uri.parse(path)
        //data source
        try {
            uriAux?.let {
                mMediaPlayer?.setDataSource(androidContext, it)
                mMediaPlayer?.prepare()
            }

        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun releasePlayer(){
        mMediaPlayer?.release()
        mMediaPlayer = null //to GC
    }


    //Controllers Actions
    fun play(){
            //prepare
        try {
            mMediaPlayer?.start()

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun pause(){
        mMediaPlayer?.let { player ->
            player.pause()
        }
    }

    fun stop() {
        mMediaPlayer?.stop()

        releasePlayer()
    }

    fun currentPosition(): Long = mMediaPlayer?.currentPosition?.toLong() ?: 0
    fun isPlaying(): Boolean = mMediaPlayer?.isPlaying ?: false
    fun setVolume(mediaVolumeDefault: Float) {
        mMediaPlayer?.setVolume(mediaVolumeDefault, mediaVolumeDefault)
    }

    fun getItemAudioCurrent() : ItemAudio? {
        return itemAudio
    }


    //State Media Player
    enum class MediaPlayerAppState{
        PLAYING,
        STOPED,
        PAUSED,
        BUFFERING
    }


}