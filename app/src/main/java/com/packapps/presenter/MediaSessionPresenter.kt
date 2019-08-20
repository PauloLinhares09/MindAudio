package com.packapps.presenter

import android.support.v4.media.session.PlaybackStateCompat
import com.packapps.audio_core.MediaPlayerApp
import com.packapps.audio_core.MediaSessionApp
import io.reactivex.subjects.PublishSubject

class MediaSessionPresenter(val mediaSessionApp: MediaSessionApp,
                            val mBuilderState : PlaybackStateCompat.Builder,
                            val mediaPlayerApp: MediaPlayerApp,
                            val publishSubject: PublishSubject<Int>
                            ) {

    init {
        mediaSessionApp.mBuilderState = mBuilderState
        mediaSessionApp.mediaPlayerApp = mediaPlayerApp
        mediaSessionApp.publishSubject = publishSubject
    }



}
