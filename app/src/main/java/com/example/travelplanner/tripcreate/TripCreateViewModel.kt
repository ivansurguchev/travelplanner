package com.example.travelplanner.tripcreate

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.NEW_LINE
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.architecture.Event
import com.example.travelplanner.base.architecture.ShowDatePickerEvent
import com.example.travelplanner.base.architecture.update
import com.example.travelplanner.base.routing.PlannerRouterHolder
import com.example.travelplanner.base.routing.TRIP_CREATION_KEY
import org.joda.time.DateTime
import javax.inject.Inject

class TripCreateViewModel(application: Application, val lifecycle: Lifecycle) :
    AndroidViewModel(application), LifecycleObserver {

    var state = MutableLiveData<CreateTripState>()
    val events = MutableLiveData<Event?>()

    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    private val context: Context = application.applicationContext

    init {
        lifecycle.addObserver(this)
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        state.postValue(CreateTripState())
    }

    fun onNameChanged(name: String) {
        state.value?.name = name
    }

    fun onStartDateFieldClicked() {
        val initialDate = state.value?.startDate ?: DateTime.now()
        val listener = { year: Int, month: Int, day: Int -> onStartDateChanged(year, month, day) }
        events.postValue(
            ShowDatePickerEvent(initialDate.year, initialDate.monthOfYear - 1, initialDate.dayOfMonth, null, null, listener))
    }

    fun onEndDateFieldClicked() {
        val initialDate = state.value?.endDate ?: state.value?.startDate ?:DateTime.now()
        val listener = { year: Int, month: Int, day: Int -> onEndDateChanged(year, month, day) }
        events.postValue(
            ShowDatePickerEvent(initialDate.year, initialDate.monthOfYear - 1, initialDate.dayOfMonth, null, null, listener))
    }

    fun onActionButtonClicked() {
        checkData()
        if (state.value?.error == null) provideResult()
    }

    fun onBackPressed() {
        routerHolder.router?.goBack()
    }

    private fun onStartDateChanged(year: Int, month: Int, day: Int ) {
        state.value?.startDate = DateTime().withDate(year, month + 1, day)
        state.update()
        events.postValue(null)
    }

    private fun onEndDateChanged(year: Int, month: Int, day: Int ) {
        state.value?.endDate = DateTime().withDate(year, month + 1, day)
        state.update()
        events.postValue(null)
    }

    private fun provideResult() {
        val result = Trip(
            System.currentTimeMillis(),
            state.value!!.name!!,
            mutableListOf(),
            state.value!!.startDate!!,
            state.value!!.endDate!!
        )
        routerHolder.router?.provideResult(TRIP_CREATION_KEY, result)
        routerHolder.router?.goBack()
    }

    private fun checkData() {
        val errorMessage = TripCreateError.values()
            .filter { it.condition(state.value) }
            .joinToString(NEW_LINE) { context.getString(it.messageRes) }
        state.value?.error = if (errorMessage.isEmpty()) null else errorMessage
        state.update()
    }
}

class TripCreateViewModelFactory(private val application: Application, private val lifecycle: Lifecycle) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripCreateViewModel(application, lifecycle) as T
    }
}

