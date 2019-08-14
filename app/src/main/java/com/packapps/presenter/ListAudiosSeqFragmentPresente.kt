package com.packapps.presenter

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.SnapHelper
import com.packapps.adapters.FragmentListAudiosSeqAdapter
import com.packapps.di.AdaptersContract
import com.packapps.repository.RepositoryLocal
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAudiosSeqFragmentPresente(private val adapter : FragmentListAudiosSeqAdapter,
                                    private val adaptersContract: AdaptersContract,
                                    private val snapHelper : SnapHelper,
                                    val repository : RepositoryLocal,
                                    val composite : CompositeDisposable) {

    private lateinit var context: Context
    private lateinit var activity: Activity

    fun adapter() = adapter

    fun adapterMain() = adaptersContract.adapterListAudios(adapter)
    fun layoutManager() = adaptersContract.layoutManager(activity)
    fun snapHelper() = adaptersContract.snapHelper(snapHelper)

    fun setContexActivity(activity: Activity){
        this.activity = activity
    }


    fun getAudioUnit(audioName : String) : Observable<String>{
        return repository.getAudioUnit(audioName)
    }



}