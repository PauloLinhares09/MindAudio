package com.di

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.MainCardAdapter


interface AdaptersContract{
    fun adapterMain(adapterMainCardAdapter: MainCardAdapter) : MainCardAdapter
    fun layoutManager(activity: Activity): LinearLayoutManager
}

class AdaptersImpl : AdaptersContract {
    override fun layoutManager(activity: Activity): LinearLayoutManager {
        return LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
    }

    override fun adapterMain(adapterMainCardAdapter: MainCardAdapter): MainCardAdapter = adapterMainCardAdapter
}
