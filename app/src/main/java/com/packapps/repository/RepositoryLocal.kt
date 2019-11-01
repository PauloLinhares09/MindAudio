package com.packapps.repository

import android.content.Intent
import androidx.lifecycle.LiveData
import com.packapps.repository.dao.PlayConfigDao
import com.packapps.repository.database.AppDatabase
import com.packapps.repository.entity.ItemAudio
import com.packapps.repository.entity.PlayConfig
import io.reactivex.Observable
import io.reactivex.Observer

class RepositoryLocal(val appDatabase: AppDatabase) {

    val listAllItemsAudioLiveData : LiveData<List<ItemAudio>>

    init {
        val itemAudioDao = appDatabase.itemAudioDao()
        listAllItemsAudioLiveData = itemAudioDao.selectAll()
    }

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


    fun getAllItemsAudioFromRoom() : LiveData<List<ItemAudio>>{
        return listAllItemsAudioLiveData
    }

    fun saveItemsAudioFromRoom(itemAudio : ItemAudio) : Long{
        val itemAudioDao = appDatabase.itemAudioDao()
        return itemAudioDao.insertItemAudio(itemAudio)
    }

    fun deleteAll() {
        appDatabase.itemAudioDao().deleteAll()
    }

    fun getPlayConfigFromItemAudio(id: Long): Observable<PlayConfig> {
        return Observable.create {sub ->
            sub.onNext(appDatabase.playConfig().selectPlayConfig(id))
            sub.onComplete()

        }

    }

    fun insertPlayConfig(playConfig: PlayConfig): Observable<Long> {
        return Observable.create { sub ->
            sub.onNext(appDatabase.playConfig().insert(playConfig))
            sub.onComplete()
        }
    }

}