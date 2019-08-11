package com.presenter

import android.app.Activity
import com.di.AdaptersProject
import com.di.HelloRepository

class MainActivityPresenter(val helloRepositoryContract: HelloRepository.HelloRepositoryContract, val adapters : AdaptersProject.AdaptersContract){

    lateinit var activity : Activity

    fun sayHello() = "${helloRepositoryContract.getHello()} from $this"

    fun adapterMain() = adapters.adapterMain()
    fun layoutManager() = adapters.layoutManager(activity)

    fun setActivityContex(activity: Activity){
        this.activity = activity
    }

}