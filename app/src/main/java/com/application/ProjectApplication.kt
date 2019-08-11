package com.application

import android.app.Application
import com.di.HelloRepository
import com.presenter.MainActivityPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

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

    val appModule = module {
        //Single instance from repository
        single<HelloRepository.HelloRepositoryContract>{HelloRepository.HelloRepositoryImpl()}

        //Factory
        factory { MainActivityPresenter(get()) }
    }
}