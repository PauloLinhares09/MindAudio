package com.packapps.repository

import android.content.Intent
import androidx.lifecycle.LiveData
import com.packapps.R
import com.packapps.repository.dao.ItemAudioDao
import com.packapps.repository.database.AppDatabase
import com.packapps.repository.entity.ItemAudio
import io.reactivex.Observable

class RepositoryLocal(val appDatabase: AppDatabase) {

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


    fun getItemsAudioFromRoom() : MutableList<ItemAudio>{
        val itemAudioDao = appDatabase.ItemAudioDao()
        return itemAudioDao.selectAll()
    }

    fun saveItemsAudioFromRoom(itemAudio : ItemAudio) : Long{
        val itemAudioDao = appDatabase.ItemAudioDao()
        return itemAudioDao.insertItemAudio(itemAudio)
    }

}