package com.packapps.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.packapps.model.dto.ItemAudioAux
import com.packapps.repository.RepositoryLocal
import com.packapps.repository.entity.ItemAudio
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

    fun getPathMedia(itemAudioAux: ItemAudioAux){
        pathAudioUnit.postValue(itemAudioAux.itemAudio?.audioPath)
    }

    fun getIntentForOpenGalery() : Intent{
        return repository.getFilesFromUserDevice()
    }

    fun getItemsAudioFromRoom() : LiveData<List<ItemAudio>>{
        return repository.getAllItemsAudioFromRoom()
    }

    fun saveItemAudioOnRoom(itemAudio: ItemAudio) : Long{
        return repository.saveItemsAudioFromRoom(itemAudio)
    }

    fun deleAllItems() {
        repository.deleteAll()
    }


}