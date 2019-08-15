package com.packapps.repository

import com.packapps.R
import io.reactivex.Observable

class RepositoryLocal {

    fun getAudioUnit(audioName : String) : Observable<Int>{

        return Observable.create { sub ->
            sub.onNext(R.raw.SampleAudio_0)
            sub.onComplete()
        }
    }

}