package com.packapps.repository

import android.content.Intent
import com.packapps.R
import io.reactivex.Observable

class RepositoryLocal {

    fun getAudioUnit(packageName : String) : Observable<String>{

        return Observable.create { sub ->
            sub.onNext("android.resource://" + packageName + "/raw/file_example")
            sub.onComplete()
        }
    }

    fun getFilesFromUserDevice() : Intent{

        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
        }
    }

}