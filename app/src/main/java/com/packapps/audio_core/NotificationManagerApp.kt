package com.packapps.audio_core

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import io.reactivex.subjects.PublishSubject

class NotificationManagerApp(
        val androidContext: Context,
        val subject: PublishSubject<Triple<Boolean, Notification, Boolean>>
    ){

    lateinit var mediaSessionApp: MediaSessionApp

    lateinit var mediaNotificationStyleApp : MediaNotificationStyleApp

    init {
        Handler().postDelayed({
            mediaNotificationStyleApp = MediaNotificationStyleApp(androidContext, mediaSessionApp)
        }, 300)

    }



        private var mServiceBrowserIsStarted: Boolean = false

        fun moveServiceToStatedState(
            playbackStateCompat: PlaybackStateCompat,
            metaDataCompat: MediaMetadataCompat) {
            val notification = mediaNotificationStyleApp.getNotification(playbackStateCompat, metaDataCompat)

            if (mServiceBrowserIsStarted) {
                ContextCompat.startForegroundService(
                    androidContext,
                    Intent(androidContext, MediaBrowserServiceApp::class.java)
                )
                mServiceBrowserIsStarted = true

            }


            subject.onNext(Triple(true, notification, false))
        }


        fun updateNotificationControllerToPause(playbackStateCompat: PlaybackStateCompat, metaDataCompat: MediaMetadataCompat){
            val notification = mediaNotificationStyleApp.getNotification(
                playbackStateCompat,
                metaDataCompat)

            subject.onNext(Triple(false, notification, false))

        }

        fun moveServiceOutOfStartedState(
            state: PlaybackStateCompat
        ) {
            mServiceBrowserIsStarted = false

            subject.onNext(Triple(false, Notification(), true))
        }


    fun getNotificationManager(): NotificationManager {
        return mediaNotificationStyleApp.notificationManager
    }

}