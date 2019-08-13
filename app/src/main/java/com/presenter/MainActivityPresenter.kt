package com.presenter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.adapters.MainCardAdapter
import com.adapters.MainCardOptionsAdapter
import com.di.AdaptersContract

class MainActivityPresenter(private val adaptersContract: AdaptersContract,
                            private val adapter : MainCardAdapter,
                            private val adapterTabOptionsAdapter: MainCardOptionsAdapter,
                            private val snapHelper: SnapHelper
){

    lateinit var activity : Activity

    fun adapterMain() = adaptersContract.adapterMain(adapter)
    fun layoutManager() = adaptersContract.layoutManager(activity)
    fun snapHelper() = adaptersContract.snapHelper(snapHelper)

    fun setActivityContex(activity: Activity){
        this.activity = activity
    }

    fun adapterMainTabOptions() = adaptersContract.adapterMainTabOptions(adapterTabOptionsAdapter)

}