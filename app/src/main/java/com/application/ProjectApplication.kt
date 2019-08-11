package com.application

import android.app.Application
import com.adapters.MainCardAdapter
import com.adapters.MainCardOptionsAdapter
import com.di.AdaptersContract
import com.di.AdaptersImpl
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
        single<AdaptersContract>{ AdaptersImpl() }
        single{ MainCardAdapter() }
        single { MainCardOptionsAdapter() }

        //Factory
        factory { MainActivityPresenter(get(), get(), get()) }
    }
}