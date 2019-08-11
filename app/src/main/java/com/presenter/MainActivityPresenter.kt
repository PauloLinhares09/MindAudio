package com.presenter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.adapters.MainCardAdapter
import com.adapters.MainCardOptionsAdapter
import com.di.AdaptersContract

class MainActivityPresenter(val adaptersContract: AdaptersContract, val adapter : MainCardAdapter, val adapterTabOptionsAdapter: MainCardOptionsAdapter){

    lateinit var activity : Activity

    fun adapterMain() = adaptersContract.adapterMain(adapter)
    fun layoutManager() = adaptersContract.layoutManager(activity)

    fun setActivityContex(activity: Activity){
        this.activity = activity
    }

    fun adapterMainTabOptions() = adaptersContract.adapterMainTabOptions(adapterTabOptionsAdapter)

}