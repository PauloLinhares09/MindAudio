package com.packapps.audio_core

import android.app.Activity
import android.content.Context
import android.media.session.MediaController
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.packapps.utils.LogApp
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


class MediaSessionApp(androidContext : Context,
                      val mBuilderState : PlaybackStateCompat.Builder,
                      val mediaPlayerApp: MediaPlayerApp,
                      val audioFocusApp: AudioFocusApp) {


    private val TAG = "MediaSessionApp"


    private lateinit var mediaSesion : MediaSessionCompat
    private var mediaSessionCallback : MediaSessionCompat.Callback



    init {

        //### Media session Callback ###
        mediaSessionCallback = object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                LogApp.i("TAG", "MediaSesion.Callback onPlay")
                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_PLAYING)
//                mediaPlayerApp.play()
                audioFocusApp.requesAudioFocus()


                mBuilderState.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())


            }

            override fun onStop() {
                super.onStop()
                LogApp.i("TAG", "MediaSesion.Callback onStop")
                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_STOPPED)

                mediaPlayerApp.stop()
                audioFocusApp.abandonAudioFocus()

                mBuilderState.setState(PlaybackStateCompat.STATE_STOPPED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())
            }

            override fun onPause() {
                super.onPause()
                LogApp.i("TAG", "MediaSesion.Callback onPause")
                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_PAUSED)

                mediaPlayerApp.pause()

                mBuilderState.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())

            }
        }

//            audioFocusApp.activity = activity
        audioFocusApp.mediaPlayerApp = mediaPlayerApp


        //Initialize my Builder State
        mBuilderState.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        mBuilderState.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f, SystemClock.elapsedRealtime())

        //Initialize MediaPlayer
//            mediaPlayerApp.context = activity

        //Create my MediaSession
        mediaSesion = MediaSessionCompat(androidContext, TAG)
        mediaSesion.setCallback(mediaSessionCallback)
        mediaSesion.setPlaybackState(mBuilderState.build())



    }

    fun loadPath(path: String) {
        mediaPlayerApp.loadMedia(path)
    }

    fun getUiControlViewModel() = audioFocusApp.uiControlsViewModel

    fun getSessionToken(): MediaSessionCompat.Token = mediaSesion.sessionToken

    fun releaseAllInstances() {
        audioFocusApp.abandonAudioFocus()
        mediaPlayerApp?.releasePlayer()
        mediaSesion.release()

    }


}