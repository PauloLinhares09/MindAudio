package com.packapps.audio_core

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import com.packapps.R
import com.packapps.utils.LogApp
import java.lang.Exception

class MediaPlayerApp {

    var mMediaPlayer : MediaPlayer? = null
    lateinit var context: Context

    private fun initializePlayer(){
        releasePlayer()
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setOnCompletionListener {player ->
            LogApp.i("MediaPlayer", "media player playback completed")
        }
    }

    fun loadMedia(path : String){
        initializePlayer()
        //file
        val uri = Uri.parse(path)
        //data source
        try {
            mMediaPlayer?.setDataSource(context, uri)
        }catch (e : Exception){
            e.printStackTrace()
        }

            //prepare
        try {
            mMediaPlayer?.prepare()
        }catch (e : Exception){
            e.printStackTrace()
        }


        //TODO init playback callback

    }

    fun releasePlayer(){
        mMediaPlayer?.release()
    }

    fun callbackPlayer(){

    }

    //Controllers Actions
    fun play(){
        mMediaPlayer?.let {player ->

        }
    }

    fun pause(){
        mMediaPlayer?.let { player ->
            player.pause()
        }
    }






}