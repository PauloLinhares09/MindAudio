package com.packapps.audio_core

import android.app.Activity
import android.media.session.MediaController
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.packapps.utils.LogApp
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


class MediaSessionApp {

    private lateinit var activity: Activity

    private val TAG = "MediaSessionApp"


    private lateinit var mediaSesion : MediaSessionCompat
    private var mediaSessionCallback : MediaSessionCompat.Callback

    private lateinit var mediaController : MediaControllerCompat
    private var mediaControllerCallback : MediaControllerCompat.Callback

    private lateinit var transportControllerCompat: MediaControllerCompat.TransportControls

    private lateinit var mBuilderState : PlaybackStateCompat.Builder

    private lateinit var mediaPlayerApp : MediaPlayerApp


    private lateinit var publishSubject : PublishSubject<Int>

    fun setContext(activity: Activity){
        this.activity = activity
    }


    init {
        publishSubject = PublishSubject.create()

        //### ControllerCallback ###
        mediaControllerCallback = object : MediaControllerCompat.Callback(){

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)

                when(state?.state){
                    PlaybackStateCompat.STATE_PLAYING -> {
                        //Change button to pause
                        LogApp.i(TAG, "STATE_PLAYING Change button to Pause")
                        publishSubject.onNext(state.state)

                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        LogApp.i(TAG, "STATE_PAUSED Change button to Play")
                        publishSubject.onNext(state.state)

                    }
                    PlaybackStateCompat.STATE_STOPPED -> {
                        LogApp.i(TAG, "STATE_STOPPED Change button to Play")
                        publishSubject.onNext(state.state)
                    }
                }

            }
        }

        //### Media session Callback ###
        mediaSessionCallback = object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                LogApp.i("TAG", "MediaSesion.Callback onPlay")
                mediaPlayerApp.play()

                mBuilderState.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())


            }

            override fun onStop() {
                super.onStop()
                LogApp.i("TAG", "MediaSesion.Callback onStop")

                mediaPlayerApp.stop()
                mBuilderState.setState(PlaybackStateCompat.STATE_STOPPED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())
            }

            override fun onPause() {
                super.onPause()
                LogApp.i("TAG", "MediaSesion.Callback onPause")

                mediaPlayerApp.pause()
                mBuilderState.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())

            }
        }

        //Initialize my Builder State
        mBuilderState = PlaybackStateCompat.Builder()
        mBuilderState.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        mBuilderState.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f, SystemClock.elapsedRealtime())

        //Initialize MediaPlayer
        mediaPlayerApp = MediaPlayerApp()
        Handler().postDelayed({ //This is necessary only to inject Koin //TODO Refactory it after
            mediaPlayerApp.context = activity

            //Create my MediaSession
            mediaSesion = MediaSessionCompat(activity!!, TAG)
            mediaSesion.setCallback(mediaSessionCallback)
            mediaSesion.setPlaybackState(mBuilderState.build())


            //Create my Media Controller
            mediaController = MediaControllerCompat(activity!!, mediaSesion)
            mediaController.registerCallback(mediaControllerCallback)
            transportControllerCompat = mediaController.transportControls

        }, 500)

    }

    fun loadPath(path: String) {
        mediaPlayerApp.loadMedia(path)
    }

    fun getStateFromMediaCrontroller(): Int = mediaController.playbackState.state
    fun getTransportController(): MediaControllerCompat.TransportControls  = transportControllerCompat
    fun fragmentOnStop() {
        if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING){
            transportControllerCompat.stop()
        }
    }

    fun fragmentOnPause() {
        if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING){
            transportControllerCompat.pause()
        }
    }

    fun fragmentOnStart() {
        transportControllerCompat.play()
    }

    fun getPublishSubject() = publishSubject


}