package com.packapps.utils

import android.util.Log
import com.packapps.BuildConfig

object LogApp {

    val debug = BuildConfig.DEBUG

    fun i(tag : String, message : String) = if(debug) { Log.i(tag, message)} else {}
    fun e(tag : String, message : String) = if(debug) { Log.e(tag, message)} else {}


}