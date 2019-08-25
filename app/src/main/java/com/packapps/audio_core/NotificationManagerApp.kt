package com.packapps.audio_core

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import io.reactivex.subjects.PublishSubject

class NotificationManagerApp(
        val androidContext: Context,
        mediaSessionApp: MediaSessionApp,
        val mediaPlayerApp: MediaPlayerApp,
        val subject: PublishSubject<Int>
    ){

    var mediaNotificationStyleApp : MediaNotificationStyleApp

    init {
        mediaNotificationStyleApp = MediaNotificationStyleApp(androidContext, mediaSessionApp)
    }



        private var mServiceBrowserIsStarted: Boolean = false

        fun moveServiceToStatedState(playbackStateCompat: PlaybackStateCompat) {
            val sessionToken = mediaNotificationStyleApp.mediaSessionApp.getSessionToken()

            val notification = mediaNotificationStyleApp.getNotification(
                playbackStateCompat,
                mediaPlayerApp.currentMedia()!!, sessionToken
            )

            if (mServiceBrowserIsStarted) {
                ContextCompat.startForegroundService(
                    androidContext,
                    Intent(androidContext, MediaBrowserServiceApp::class.java)
                )
                mServiceBrowserIsStarted = true

            }


//            subject.onNext(Triple(true, notification, false))
        }


        fun updateNotificationControllerToPause(playbackStateCompat: PlaybackStateCompat){
            val sessionToken = mediaNotificationStyleApp.mediaSessionApp.getSessionToken()
            val notification = mediaNotificationStyleApp.getNotification(
                playbackStateCompat,
                mediaPlayerApp.currentMedia()!!, sessionToken)

        }

        fun moveServiceOutOfStartedState(state: PlaybackStateCompat) {
            mServiceBrowserIsStarted = false
        }

    fun getNotification(
        playbackState: PlaybackStateCompat,
        metadata: MediaMetadataCompat,
        sessionToken: MediaSessionCompat.Token
    ): Notification? {
        return mediaNotificationStyleApp.getNotification(playbackState, metadata, sessionToken)
    }

}