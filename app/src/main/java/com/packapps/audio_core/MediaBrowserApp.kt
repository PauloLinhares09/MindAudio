package com.packapps.audio_core

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.ViewModel
import com.packapps.utils.LogApp
import com.packapps.viewmodel.UiControlsViewModel
import io.reactivex.subjects.PublishSubject
import java.lang.Exception

class MediaBrowserApp(
    androidContext: Context,
    private val mediaSessionApp: MediaSessionApp,
    val publishSubject : PublishSubject<Int> ) {

    val TAG = "MediaBrowserApp"

    private lateinit var mediaController : MediaControllerCompat





    private var mediaBrowser : MediaBrowserCompat

    init {
         val connectionCallback: MediaBrowserCompat.ConnectionCallback = object : MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                LogApp.i(TAG, "onConnected")

                try {

                    mediaController = MediaControllerCompat(androidContext, mediaSessionApp.getSessionToken())

//                    MediaControllerCompat.setMediaController(activity, mediaController)


                }catch (e : Exception){
                    e.printStackTrace()
                }

            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
                LogApp.e(TAG, "onConnectionSuspended")
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
                LogApp.e(TAG, "onConnectionFailed")
            }
        }

        //instantiete MediaBrowser
        mediaBrowser = MediaBrowserCompat(androidContext,
            ComponentName(androidContext, MediaBrowserServiceApp::class.java),
            connectionCallback
            , null)
        mediaBrowser.connect()
    }



    fun getTransportController(): MediaControllerCompat.TransportControls  = mediaController.transportControls

    fun getStateFromMediaCrontroller(): Int  = mediaController.playbackState.state

    fun getUiControlViewModel(): UiControlsViewModel  = mediaSessionApp.getUiControlViewModel()

    fun loadPath(path: String) {
        mediaSessionApp.loadPath(path)
    }


}
