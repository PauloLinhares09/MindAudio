package com.packapps.repository

import com.packapps.R
import io.reactivex.Observable

class RepositoryLocal {

    fun getAudioUnit(packageName : String) : Observable<String>{

        return Observable.create { sub ->
            sub.onNext("android.resource://" + packageName + "/raw/file_example")
            sub.onComplete()
        }
    }

}