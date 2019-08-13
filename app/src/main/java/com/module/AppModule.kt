package com.module

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.adapters.MainCardAdapter
import com.adapters.MainCardOptionsAdapter
import com.di.AdaptersContract
import com.di.AdaptersImpl
import com.presenter.MainActivityPresenter
import org.koin.dsl.module

val appModule = module {
    //Single instance from repository
    single<AdaptersContract>{ AdaptersImpl() }
    single{ MainCardAdapter() }
    single { MainCardOptionsAdapter() }
    single<SnapHelper> { LinearSnapHelper() }

    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
}