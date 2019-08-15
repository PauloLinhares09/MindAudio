package com.packapps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.repository.RepositoryLocal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class ListAudioSeqFragmentViewModel : ViewModel() {

    lateinit var composite : CompositeDisposable
    lateinit var repository : RepositoryLocal

    //livedata
    val pathAudioUnit = MutableLiveData<Int>()

    fun getAudioUni(audioName : String){
        composite.add(
            repository.getAudioUnit(audioName)
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