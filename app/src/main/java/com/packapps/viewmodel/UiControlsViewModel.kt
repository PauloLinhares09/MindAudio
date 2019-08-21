package com.packapps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UiControlsViewModel() : ViewModel(){

    val stateControls = MutableLiveData<Int>()

}