package com.di

class HelloRepository {

    interface HelloRepositoryContract{
        fun getHello() : String
    }

    class HelloRepositoryImpl : HelloRepositoryContract{
        override fun getHello(): String {
            return "Hello, Paulo"
        }
    }


}