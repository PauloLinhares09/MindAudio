package com.packapps.dto

class CardTab (val name : String, item : Any, val typeCardTab : Int)




enum class TypeCardTab(val code : Int){
    MAIN_LIST_EMPTY(0),
    MAIN_LIST(1)
}