package com.di

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.MainCardAdapter

class AdaptersProject{

    interface AdaptersContract{
        fun adapterMain() : MainCardAdapter
        fun layoutManager(activity: Activity): RecyclerView.LayoutManager
    }

    class AdaptersImpl : AdaptersContract {
        override fun layoutManager(activity: Activity): RecyclerView.LayoutManager {
            return LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        }

        override fun adapterMain(): MainCardAdapter {
            val adapter = MainCardAdapter()

            return adapter
        }
    }
}