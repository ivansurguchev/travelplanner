package com.example.travelplanner.tripinfo

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.Action
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.data.PlannerInteractor
import com.example.travelplanner.base.routing.PlannerRouterHolder
import com.example.travelplanner.tripslist.TripItem
import com.example.travelplanner.tripslist.toItem
import javax.inject.Inject

class TripInfoViewModel(application: Application, lifecycle: Lifecycle, private val tripId: Long)
    : AndroidViewModel(application), LifecycleObserver {

    var tripSatate = MutableLiveData<TripItem>()
    var actionsState = MutableLiveData<List<Item>>()

    @Inject
    internal lateinit var plannerInteractor: PlannerInteractor
    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    init {
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        lifecycle.addObserver(this)
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        plannerInteractor
            .getTrip(tripId)
            .subscribe({ onTripLoaded(it) }, { it.printStackTrace() })
    }

    fun onCreateActionClicked() {
        routerHolder.router?.goToActionCreation(tripId) { onActionCreated(it as Action) }
    }

    fun onBackPressed() {
        routerHolder.router?.goBack()
    }

    private fun onTripLoaded(trip: Trip) {
        tripSatate.postValue(trip.toItem())
        actionsState.postValue(trip.actions.toItems())
    }

    @SuppressLint("CheckResult")
    private fun onActionCreated(action: Action) {
        plannerInteractor
            .addTripAction(tripId, action)
            .map { it.actions.toItems() }
            .subscribe({ actionsState.postValue(it) }, { it.printStackTrace() })
    }
}

class TripInfoViewModelFactory(private val application: Application, private val tripId: Long,
                               private val lifecycle: Lifecycle) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripInfoViewModel(application, lifecycle, tripId) as T
    }
}