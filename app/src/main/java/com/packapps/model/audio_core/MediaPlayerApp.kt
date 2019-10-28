package com.packapps.model.audio_core

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri


class MediaPlayerApp(val androidContext: Context) {


    var mMediaPlayer : MediaPlayer? = null

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
            mMediaPlayer?.setDataSource(androidContext, uri)

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
    fun isPlaying(): Boolean = mMediaPlayer?.isPlaying ?: false
    fun setVolume(mediaVolumeDefault: Float) {
        mMediaPlayer?.setVolume(mediaVolumeDefault, mediaVolumeDefault)
    }


    //State Media Player
    enum class MediaPlayerAppState{
        PLAYING,
        STOPED,
        PAUSED,
        BUFFERING
    }


}