package com.packapps.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_config")
class PlayConfig (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id : Long,

    @ColumnInfo(name = "item_audio_id")
    val itemAudioId : Long,

    @ColumnInfo(name = "how_many_times")
    val howManyTimes : Int,

    @ColumnInfo(name = "time_current")
    val timeCurrent : Int

)