package com.packapps.di

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.packapps.adapters.FragmentListAudiosSeqAdapter
import com.packapps.adapters.MainCardAdapter
import com.packapps.adapters.MainCardOptionsAdapter


interface AdaptersContract{
    fun adapterMain(adapterMainCardAdapter: MainCardAdapter) : MainCardAdapter
    fun layoutManager(activity: Activity): LinearLayoutManager
    fun adapterMainTabOptions(adapterTabOptionsAdapter: MainCardOptionsAdapter): MainCardOptionsAdapter
    fun snapHelper(snapHelper: SnapHelper): SnapHelper
    fun adapterListAudios(adapter: FragmentListAudiosSeqAdapter): FragmentListAudiosSeqAdapter
}

class AdaptersImpl : AdaptersContract {
    override fun adapterListAudios(adapter: FragmentListAudiosSeqAdapter) = adapter

    override fun snapHelper(snapHelper: SnapHelper): SnapHelper = snapHelper

    override fun adapterMainTabOptions(adapterTabOptionsAdapter: MainCardOptionsAdapter): MainCardOptionsAdapter  =  adapterTabOptionsAdapter

    override fun layoutManager(activity: Activity): LinearLayoutManager {
        return LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
    }

    override fun adapterMain(adapterMainCardAdapter: MainCardAdapter): MainCardAdapter = adapterMainCardAdapter
}
