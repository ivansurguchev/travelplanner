package com.example.travelplanner.base.architecture

import androidx.lifecycle.MutableLiveData

fun <T : Any> MutableLiveData<T>.update() {
    postValue(value)
}