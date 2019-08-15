package com.packapps

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ProjectApplication : Application(){


    override fun onCreate() {
        super.onCreate()

        //start Koin
        startKoin {
            androidLogger()
            androidContext(this@ProjectApplication)
            modules(appModule)


        }
    }


}