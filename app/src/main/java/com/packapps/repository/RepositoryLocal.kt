package com.packapps.repository

import io.reactivex.Observable

class RepositoryLocal {

    fun getAudioUnit(audioName : String) : Observable<String>{

        return Observable.create { sub ->
            sub.onNext(audioName)
            sub.onComplete()
        }
    }

}