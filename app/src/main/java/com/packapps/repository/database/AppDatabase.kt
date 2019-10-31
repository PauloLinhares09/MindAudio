package com.packapps.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.packapps.repository.dao.ItemAudioDao
import com.packapps.repository.entity.ItemAudio

@Database(entities = arrayOf(ItemAudio::class), version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun itemAudioDao() : ItemAudioDao

    companion object {
        var INSTANCE : AppDatabase? = null

        fun getDatabaseBuilder(context: Context) : AppDatabase{
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "database_app")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!

        }

        fun destroyInstance(){
            INSTANCE = null
        }

    }

}
