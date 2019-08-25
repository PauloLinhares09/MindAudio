package com.packapps

import android.app.Notification
import android.support.v4.media.session.PlaybackStateCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.packapps.adapters.FragmentListAudiosSeqAdapter
import com.packapps.adapters.MainCardAdapter
import com.packapps.adapters.MainCardOptionsAdapter
import com.packapps.audio_core.*
import com.packapps.di.AdaptersContract
import com.packapps.di.AdaptersImpl
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.presenter.MainActivityPresenter
import com.packapps.repository.RepositoryLocal
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.dsl.module

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named

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
    single { MediaPlayerApp(androidContext()) }
    single(named("int")) { PublishSubject.create<Int>() }
    single(named("triple")) { PublishSubject.create<Triple<Boolean, Notification, Boolean>>() }
    single { AudioFocusApp(androidContext()) }
    single { NotificationManagerApp(androidContext(), get(named("triple"))) }

    //Repository
    single { RepositoryLocal() }

    //RX
    single { CompositeDisposable() }



    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
    factory { ListAudiosSeqFragmentPresente(get(), get(), get(), get(), get()) }
    factory { MediaSessionApp(androidContext() , get(), get(), get(), get()) }
    factory { MediaBrowserApp(androidContext(), get(), get(named("int"))) }



}

