package com.packapps.model.dto

import com.packapps.model.audio_core.MediaPlayerApp
import com.packapps.repository.entity.ItemAudio

open class AuxAdapter {
    var itemAudio : ItemAudio? = null
    var currentPosition : Int? = null
    var currentStatePlayback: MediaPlayerApp.MediaPlayerAppState? = null
}
