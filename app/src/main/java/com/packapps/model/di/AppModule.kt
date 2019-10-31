package com.packapps

import android.app.Notification
import android.support.v4.media.session.PlaybackStateCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.packapps.model.audio_core.*
import com.packapps.model.di.AdaptersContract
import com.packapps.model.di.AdaptersImpl
import com.packapps.model.presenter.ListAudiosSeqFragmentPresente
import com.packapps.model.presenter.MainActivityPresenter
import com.packapps.repository.RepositoryLocal
import com.packapps.repository.database.AppDatabase
import com.packapps.ui.adapters.FragmentListAudiosSeqAdapter
import com.packapps.ui.adapters.MainCardAdapter
import com.packapps.ui.adapters.MainCardOptionsAdapter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    //Single instance from repository
    single<AdaptersContract>{ AdaptersImpl() }
    single{ MainCardAdapter() }
    single { MainCardOptionsAdapter(PublishSubject.create<Int>()) }
    single<SnapHelper> { LinearSnapHelper() }

    //### For fragments
    single{ FragmentListAudiosSeqAdapter() }
    //MediaSessionAPP
    single { PlaybackStateCompat.Builder() }
    single { MediaPlayerApp(androidContext(), PublishSubject.create<Boolean>()) }
    single(named("int")) { PublishSubject.create<Int>() }
    single(named("triple")) { PublishSubject.create<Triple<Boolean, Notification, Boolean>>() }
    single { AudioFocusApp(androidContext()) }
    single {
        NotificationManagerApp(
            androidContext(),
            get(named("triple"))
        )
    }
    single { MediaBroadcastNotificationActions(PublishSubject.create<String>()) }
    //Room
    single { AppDatabase.getDatabaseBuilder(androidContext()) }

    //Repository
    single { RepositoryLocal(get()) }

    //RX
    single { CompositeDisposable() }



    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
    factory { ListAudiosSeqFragmentPresente(get(), get(), get(), get(), get(), get()) }
    factory { MediaSessionApp(androidContext() , get(), get(), get(), get()) }
    factory {
        MediaBrowserApp(
            androidContext(),
            get(),
            get(named("int"))
        )
    }





}

