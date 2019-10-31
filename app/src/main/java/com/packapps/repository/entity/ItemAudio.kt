package com.packapps.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "item_audio")
class ItemAudio   (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "list_name")
    val listName: String,

    @ColumnInfo(name = "audio_name")
    val audioName: String,

    @ColumnInfo(name = "audio_path")
    val audioPath: String? = null )


