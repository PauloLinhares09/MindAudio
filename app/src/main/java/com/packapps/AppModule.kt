package com.packapps

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.packapps.adapters.FragmentListAudiosSeqAdapter
import com.packapps.adapters.MainCardAdapter
import com.packapps.adapters.MainCardOptionsAdapter
import com.packapps.di.AdaptersContract
import com.packapps.di.AdaptersImpl
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.presenter.MainActivityPresenter
import com.packapps.repository.RepositoryLocal
import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module

import org.koin.android.architecture.ext.android.viewModel

val appModule = module {
    //Single instance from repository
    single<AdaptersContract>{ AdaptersImpl() }
    single{ MainCardAdapter() }
    single { MainCardOptionsAdapter() }
    single<SnapHelper> { LinearSnapHelper() }

    //fragments
    single{ FragmentListAudiosSeqAdapter() }

    //Repository
    single { RepositoryLocal() }

    //RX
    single { CompositeDisposable() }



    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
    factory { ListAudiosSeqFragmentPresente(get(), get(), get(), get(), get()) }

}

