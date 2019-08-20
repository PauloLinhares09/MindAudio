package com.packapps

import android.support.v4.media.session.PlaybackStateCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.packapps.adapters.FragmentListAudiosSeqAdapter
import com.packapps.adapters.MainCardAdapter
import com.packapps.adapters.MainCardOptionsAdapter
import com.packapps.audio_core.MediaPlayerApp
import com.packapps.audio_core.MediaSessionApp
import com.packapps.di.AdaptersContract
import com.packapps.di.AdaptersImpl
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.presenter.MainActivityPresenter
import com.packapps.presenter.MediaSessionPresenter
import com.packapps.repository.RepositoryLocal
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.dsl.module

import org.koin.android.architecture.ext.android.viewModel

val appModule = module {
    //Single instance from repository
    single<AdaptersContract>{ AdaptersImpl() }
    single{ MainCardAdapter() }
    single { MainCardOptionsAdapter() }
    single<SnapHelper> { LinearSnapHelper() }

    //### For fragments
    single{ FragmentListAudiosSeqAdapter() }
    //MediaSessionAPP
    single { PlaybackStateCompat.Builder() }
    single { MediaSessionApp() }
    single { MediaPlayerApp() }
    single { PublishSubject.create<Int>() }

    //Repository
    single { RepositoryLocal() }

    //RX
    single { CompositeDisposable() }



    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
    factory { ListAudiosSeqFragmentPresente(get(), get(), get(), get(), get()) }
    factory { MediaSessionPresenter(get(), get(), get(), get()) }

}

