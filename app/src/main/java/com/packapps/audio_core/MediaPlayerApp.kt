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

    //state
    private val subjectState = PublishSubject.create<MediaPlayerAppState>()

    private fun initializePlayer(){
        releasePlayer()
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setOnCompletionListener {player ->
            LogApp.i("MediaPlayer", "media player playback completed")
            if (player.isPlaying)
                subjectState.onNext(MediaPlayerAppState.PLAYING)
            else
                subjectState.onNext(MediaPlayerAppState.PAUSED)
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



    }

    fun releasePlayer(){
        mMediaPlayer?.release()
        mMediaPlayer = null //to GC
    }


    //Controllers Actions
    fun play(){
            //prepare
        try {
            mMediaPlayer?.prepare()
            mMediaPlayer?.start()

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun pause(){
        mMediaPlayer?.let { player ->
            player.pause()
            subjectState.onNext(MediaPlayerAppState.PAUSED)
        }
    }

    fun stop() {
        mMediaPlayer?.stop()
        subjectState.onNext(MediaPlayerAppState.STOPED)
    }

    fun getSubjectState() = subjectState
    fun currentPosition(): Long = mMediaPlayer?.currentPosition?.toLong() ?: 0

    //State Media Player
    enum class MediaPlayerAppState{
        PLAYING,
        STOPED,
        PAUSED,
        BUFFERING
    }


}