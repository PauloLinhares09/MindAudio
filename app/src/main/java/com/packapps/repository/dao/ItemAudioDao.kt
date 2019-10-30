package com.packapps.repository.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.packapps.repository.entity.ItemAudio

@Dao
interface ItemAudioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItemAudio(itemAudio : ItemAudio) : Long

    @Query("DELETE FROM item_audio")
    fun deleteAll()

    @Query("SELECT * FROM item_audio ORDER BY id ASC")
    fun selectAll() : MutableLiveData<MutableList<ItemAudio>>

}