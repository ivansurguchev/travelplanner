package com.example.travelplanner.base.architecture

enum class LoadingState { LOADING, LOADED, ERROR }

class LoadingScreenState<D, E>(var loadingState: LoadingState, var loadedData: D? = null, var error: E? = null)

open class Event

class ShowDatePickerEvent(val year: Int, val month: Int, val day: Int, val minDateMillis: Long? = null, val maxDateMillis: Long? = null,
                          val listener: (Int, Int, Int) -> Unit): Event()

class ShowTimePickerEvent(val hour: Int, val minute: Int, val listener: (Int, Int) -> Unit): Event()
