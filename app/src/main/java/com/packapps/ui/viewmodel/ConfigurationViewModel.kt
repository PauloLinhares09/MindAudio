package com.packapps.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.packapps.repository.RepositoryLocal
import com.packapps.repository.entity.PlayConfig
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class ConfigurationViewModel : ViewModel(){

    val compositeDisposable : CompositeDisposable = CompositeDisposable()

    val playConfig : MutableLiveData<PlayConfig> = MutableLiveData()
    val isSaved : MutableLiveData<Boolean> = MutableLiveData()
    lateinit var repositoryLocal : RepositoryLocal


    fun getPlayConfig(audioItemId : Long){

        compositeDisposable.add(
            repositoryLocal.getPlayConfigFromItemAudio(audioItemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({audioPath ->
                    playConfig.postValue(audioPath)

                },
                    {
                        it.printStackTrace()
                    }))
    }

    fun savePlayConfig(playConfig: PlayConfig): Long {

        compositeDisposable.add(
            (repositoryLocal.insertPlayConfig(playConfig)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({id ->
                    if (id > 0)
                        isSaved.postValue(true)
                    else
                        isSaved.postValue(false)

                },
                    {
                        it.printStackTrace()
                    }))
    }

}