package com.packapps.repository.dao

import androidx.room.*
import com.packapps.repository.entity.PlayConfig


@Dao
interface PlayConfigDao{

    @Query("SELECT * FROM play_config WHERE item_audio_id = :itemAudioId")
    fun selectPlayConfig(itemAudioId : Long) : PlayConfig

    @Query("DELETE from play_config WHERE id = :id")
    fun deletePlayConfig(id : Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    fun insert(playConfig: PlayConfig) : Long


}