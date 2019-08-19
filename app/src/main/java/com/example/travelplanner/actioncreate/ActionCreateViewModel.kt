package com.example.travelplanner.actioncreate

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.Action
import com.example.travelplanner.base.NEW_LINE
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.architecture.Event
import com.example.travelplanner.base.architecture.ShowDatePickerEvent
import com.example.travelplanner.base.architecture.ShowTimePickerEvent
import com.example.travelplanner.base.architecture.update
import com.example.travelplanner.base.data.PlannerInteractor
import com.example.travelplanner.base.routing.ACTION_CREATION_KEY
import com.example.travelplanner.base.routing.PlannerRouterHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime
import javax.inject.Inject

class ActionCreateViewModel(application: Application, lifecycle: Lifecycle, tripId: Long)
    : AndroidViewModel(application), LifecycleObserver {

    var state = MutableLiveData<ActionCreateState>()
    var events = MutableLiveData<Event?>()

    @Inject
    internal lateinit var plannerInteractor: PlannerInteractor
    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    private val context: Context = application.applicationContext
    private val tripSubject: BehaviorSubject<Trip> = BehaviorSubject.create()
    private var compositeDisposable = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        val tripDisposable = plannerInteractor
            .getTrip(tripId)
            .subscribe({tripSubject.onNext(it)}, {tripSubject.onError(it)})
        compositeDisposable.add(tripDisposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        state.postValue(ActionCreateState())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        compositeDisposable.dispose()
    }

    fun onNameChanged(name: String) {
        state.value?.name = name
    }

    fun onDateFieldClicked() {
        val tripDisposable = tripSubject.subscribe({ showDatePicker(it) }, { it.printStackTrace() })
        compositeDisposable.add(tripDisposable)
    }

    private fun showDatePicker(trip: Trip) {
        val initialDate = state.value?.date ?: trip.startsAt
        val listener = { year: Int, month: Int, day: Int -> onDateChanged(year, month,day) }
        events.postValue(
            ShowDatePickerEvent(
                initialDate.year, initialDate.monthOfYear - 1, initialDate.dayOfMonth,
                trip.startsAt.millis, trip.endsAt.millis, listener)
        )
    }

    fun onStartTimeFieldClicked() {
        val initialHours = state.value?.startHours ?: 12
        val initialMinutes = state.value?.startMinutes ?: 0
        events.postValue(
            ShowTimePickerEvent(initialHours, initialMinutes) { hours, minutes -> onStartTimeChanged(hours, minutes) })
    }

    fun onEndTimeFieldClicked() {
        val initialHours = state.value?.endHours ?: state.value?.startHours ?: 13
        val initialMinutes = state.value?.endMinutes ?: state.value?.startMinutes ?: 0
        events.postValue(
            ShowTimePickerEvent(initialHours, initialMinutes) { hours, minutes -> onEndTimeChanged(hours, minutes) })
    }

    fun onActionButtonClicked() {
        checkData()
        if (state.value?.error == null) provideResult()
    }

    private fun onDateChanged(year: Int, month: Int, day: Int) {
        val date = DateTime().withYear(year).withMonthOfYear(month + 1).withDayOfMonth(day)
        state.value?.date = date
        state.update()
        events.postValue(null)
    }

    private fun onStartTimeChanged(hours: Int, minutes: Int) {
        state.value?.startHours = hours
        state.value?.startMinutes = minutes
        state.update()
        events.postValue(null)
    }

    private fun onEndTimeChanged(hours: Int, minutes: Int?) {
        state.value?.endHours = hours
        state.value?.endMinutes = minutes
        state.update()
        events.postValue(null)
    }

    private fun provideResult() {
        val startDate = state.value!!.date!!.withHourOfDay(state.value!!.startHours!!).withMinuteOfHour(state.value!!.startMinutes!!)
        val endDate = state.value!!.date!!.withHourOfDay(state.value!!.endHours!!).withMinuteOfHour(state.value!!.endMinutes!!)
        val result = Action(state.value!!.name!!, startDate, endDate)
        routerHolder.router?.provideResult(ACTION_CREATION_KEY, result)
        routerHolder.router?.goBack()
    }

    private fun checkData() {
        val errorMessage = ActionCreateError.values()
            .filter { it.condition(state.value) }
            .joinToString(NEW_LINE) { context.getString(it.messageRes) }
        state.value?.error = if (errorMessage.isEmpty()) null else errorMessage
        state.update()
    }
}
