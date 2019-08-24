package com.packapps.audio_core

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.packapps.R
import org.koin.android.ext.android.inject

class MediaBrowserServiceApp : MediaBrowserServiceCompat() {



    val mediaSessionApp : MediaSessionApp by inject()


    override fun onCreate() {
        super.onCreate()

        Handler().postDelayed({
            setSessionToken(mediaSessionApp.getSessionToken())
        }, 800)


        //Create Notification Builder foregrownd


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