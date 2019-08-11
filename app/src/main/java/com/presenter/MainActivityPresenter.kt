package com.presenter

import com.di.HelloRepository

class MainActivityPresenter(val helloRepositoryContract: HelloRepository.HelloRepositoryContract){

    fun sayHello() = "${helloRepositoryContract.getHello()} from $this"

}