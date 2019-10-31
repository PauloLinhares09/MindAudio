package com.packapps.model.audio_core

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.packapps.model.utils.LogApp
import io.reactivex.subjects.PublishSubject


class MediaSessionApp(androidContext : Context,
                      val mBuilderState : PlaybackStateCompat.Builder,
                      val mediaPlayerApp: MediaPlayerApp,
                      val audioFocusApp: AudioFocusApp,
                      val notificaManagerApp : NotificationManagerApp
                      ) {


    private val TAG = "MediaSessionApp"


    private lateinit var mediaSesion : MediaSessionCompat
    private var mediaSessionCallback : MediaSessionCompat.Callback



    init {
        notificaManagerApp.mediaSessionApp = this

        val metaDataCompat = MusicLibraryUtil.getMetadata(androidContext, MusicLibraryUtil.idMock())


        //### Media session Callback ###
        mediaSessionCallback = object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                LogApp.i("TAG", "MediaSesion.Callback onPlay")
                mediaSesion.isActive = true

                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_PLAYING)
//                mediaPlayerApp.play()
                audioFocusApp.requesAudioFocus()


                mBuilderState.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())



                notificaManagerApp.moveServiceToStatedState(getPlaybackState(), metaDataCompat)

            }

            override fun onStop() {
                super.onStop()
                LogApp.i("TAG", "MediaSesion.Callback onStop")
                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_STOPPED)

                mediaPlayerApp.stop()
                audioFocusApp.abandonAudioFocus()

                mBuilderState.setState(PlaybackStateCompat.STATE_STOPPED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())

                notificaManagerApp.moveServiceOutOfStartedState(getPlaybackState())

                mediaSesion.isActive = false

            }

            override fun onPause() {
                super.onPause()
                LogApp.i("TAG", "MediaSesion.Callback onPause")
                getUiControlViewModel().stateControls.postValue(PlaybackStateCompat.STATE_PAUSED)

                mediaPlayerApp.pause()

                mBuilderState.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())

                notificaManagerApp.updateNotificationControllerToPause(getPlaybackState(), metaDataCompat)

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

    fun loadPath(path: String, uri : Uri? = null) {
        mediaPlayerApp.loadMedia(path, uri)
    }

    fun listenEndMediaPlayer() : PublishSubject<Boolean>{
        return mediaPlayerApp.subject
    }

    fun getUiControlViewModel() = audioFocusApp.uiControlsViewModel

    fun getSessionToken(): MediaSessionCompat.Token = mediaSesion.sessionToken

    fun releaseAllInstances() {
        audioFocusApp.abandonAudioFocus()
        mediaPlayerApp?.releasePlayer()
        mediaSesion.release()

    }

    fun getPlaybackState(): PlaybackStateCompat = mediaSesion.controller.playbackState



}