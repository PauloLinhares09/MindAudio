package com.packapps

import android.support.v4.media.session.PlaybackStateCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.packapps.ui.adapters.FragmentListAudiosSeqAdapter
import com.packapps.ui.adapters.MainCardAdapter
import com.packapps.ui.adapters.MainCardOptionsAdapter
import com.packapps.model.audio_core.AudioFocusApp
import com.packapps.model.audio_core.MediaPlayerApp
import com.packapps.model.audio_core.MediaSessionApp
import com.packapps.model.di.AdaptersContract
import com.packapps.model.di.AdaptersImpl
import com.packapps.model.presenter.ListAudiosSeqFragmentPresente
import com.packapps.model.presenter.MainActivityPresenter
import com.packapps.repository.RepositoryLocal
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.dsl.module

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
    single { MediaPlayerApp() }
    single { PublishSubject.create<Int>() }
    single { AudioFocusApp() }

    //Repository
    single { RepositoryLocal() }

    //RX
    single { CompositeDisposable() }



    //Factory
    factory { MainActivityPresenter(get(), get(), get(), get()) }
    factory { ListAudiosSeqFragmentPresente(get(), get(), get(), get(), get()) }
    factory { MediaSessionApp(get(), get(), get(), get()) }

}

