package com.packapps.repository

import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import org.json.JSONObject

class RepositoryLocal {


    fun getAudioUnit(audioName : String) : Observable<String>{

        return Observable.create { sub ->
            sub.onNext("Paulo Linhares")
            sub.onComplete()
        }
    }

}