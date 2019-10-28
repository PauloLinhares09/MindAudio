package com.packapps.model.audio_core

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.packapps.model.utils.LogApp
import com.packapps.ui.viewmodel.UiControlsViewModel

class AudioFocusApp : ViewModelStoreOwner {
    override fun getViewModelStore(): ViewModelStore {
        return ViewModelStore()
    }

    private val TAG = "AudioFocusApp"

    private val MEDIA_VOLUME_DEFAULT = 1.0f
    private val MEDIA_VOLUME_DUCK = 0.2f

    private var mAudioFocusIsGranted: Boolean = false
    lateinit var activity : Activity
    lateinit var mediaPlayerApp: MediaPlayerApp

    lateinit var audioManager : AudioManager
    lateinit var audioFocusRequest: AudioFocusRequest

    lateinit var uiControlsViewModel: UiControlsViewModel


    init {
        Handler().postDelayed({
            audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            uiControlsViewModel = ViewModelProvider(this).get(UiControlsViewModel::class.java)

            val audioAttributes = AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build() }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                    setAudioAttributes(audioAttributes)
                    setAcceptsDelayedFocusGain(true)
                    setOnAudioFocusChangeListener(audioFocusChangeListener, Handler())


                    build()
                }
            }

        }, 600)
    }

    private val audioFocusChangeListener = object : AudioManager.OnAudioFocusChangeListener{
        override fun onAudioFocusChange(focusState: Int) {

            when(focusState){
                AudioManager.AUDIOFOCUS_GAIN -> {
                    LogApp.i(TAG, "AUDIOFOCUS_GAIN")

                    if (mAudioFocusIsGranted && !isPlaying()){
                        mediaPlayerApp.play()
                        uiControlsViewModel.stateControls.postValue(PlaybackStateCompat.STATE_PLAYING)
                    } else if (isPlaying()){
                        setVolume(MEDIA_VOLUME_DEFAULT)
                    }
                    mAudioFocusIsGranted = false

                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    LogApp.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")

                    if (isPlaying()){
                        mAudioFocusIsGranted = true
                        mediaPlayerApp.pause()
                        uiControlsViewModel.stateControls.postValue(PlaybackStateCompat.STATE_PAUSED)
                    }

                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    LogApp.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                    setVolume(MEDIA_VOLUME_DUCK)

                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    LogApp.i(TAG, "AUDIOFOCUS_LOSS")
                    abandonAudioFocus()
                    mAudioFocusIsGranted = false
                    mediaPlayerApp.stop()
                    uiControlsViewModel.stateControls.postValue(PlaybackStateCompat.STATE_STOPPED)

                }

            }
        }
    }

    private fun setVolume(mediaVolumeDefault: Float) {
        mediaPlayerApp.setVolume(mediaVolumeDefault)
    }

    private fun isPlaying(): Boolean = mediaPlayerApp.isPlaying()



    fun requesAudioFocus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = audioManager.requestAudioFocus(audioFocusRequest)

            when(focusRequest){
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                    LogApp.i(TAG, "AUDIOFOCUS_REQUEST_GRANTED")
                    //Don't start playback

                }
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    LogApp.i(TAG, "AUDIOFOCUS_REQUEST_GRANTED")
                    //start playback
                    mediaPlayerApp.play()
                    uiControlsViewModel.stateControls.postValue(PlaybackStateCompat.STATE_PLAYING)
                }
            }
        }
    }




    fun abandonAudioFocus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        }
    }


}