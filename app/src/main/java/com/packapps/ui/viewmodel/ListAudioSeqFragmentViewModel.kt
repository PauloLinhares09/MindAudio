package com.packapps.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.packapps.repository.RepositoryLocal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAudioSeqFragmentViewModel : ViewModel() {

    lateinit var composite : CompositeDisposable
    lateinit var repository : RepositoryLocal

    //livedata
    val pathAudioUnit = MutableLiveData<String>()

    fun getAudioUni(packageName : String){
        composite.add(
            repository.getAudioUnit(packageName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({audioPath ->
                    pathAudioUnit.postValue(audioPath)

        },
                    {
                        it.printStackTrace()
                    }))
    }




}