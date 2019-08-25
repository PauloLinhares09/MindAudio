package com.packapps.audio_core

import android.app.Activity
import android.app.Notification
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.packapps.R
import com.packapps.dto.ItemAudio
import com.packapps.utils.LogApp
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.koin.android.ext.android.inject

class MediaBrowserServiceApp : MediaBrowserServiceCompat() {

    val NOTIFICATION_ID = 1009

    lateinit var notificationManagerApp: NotificationManagerApp
    val TAG = "MediaBrowserServiceApp"



    val mediaSessionApp : MediaSessionApp by inject()


    override fun onCreate() {
        super.onCreate()

        Handler().postDelayed({
            setSessionToken(mediaSessionApp.getSessionToken())
        }, 800)


        //Create Notification Builder foregrownd

        val s =  mediaSessionApp.notificaManagerApp.subject.subscribe { triple ->
            LogApp.i(TAG, "subject: ${triple.first}")

            if (triple.first){

                startForeground(NOTIFICATION_ID, triple.second)

            }else if (triple.first == false && triple.third == false){

                stopForeground(false)
                val manager = notificationManagerApp.getNotificationManager()
                manager.notify(NOTIFICATION_ID, triple.second)

            }else if (triple.third){

                stopForeground(true)
                stopSelf()
            }

        }

        notificationManagerApp = mediaSessionApp.notificaManagerApp


    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSessionApp.releaseAllInstances()
    }



    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        return result.sendResult(null)
    }




}