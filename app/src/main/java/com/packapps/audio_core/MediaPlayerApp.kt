package com.packapps.audio_core

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import com.packapps.R
import com.packapps.utils.LogApp
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.Exception

class MediaPlayerApp {


    var mMediaPlayer : MediaPlayer? = null
    lateinit var context: Context

    private fun initializePlayer(){
        releasePlayer()
        mMediaPlayer = MediaPlayer()
    }

    fun loadMedia(path : String){
        initializePlayer()
        //file
        val uri = Uri.parse(path)
        //data source
        try {
            mMediaPlayer?.setDataSource(context, uri)

            mMediaPlayer?.prepare()

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

    //State Media Player
    enum class MediaPlayerAppState{
        PLAYING,
        STOPED,
        PAUSED,
        BUFFERING
    }


}