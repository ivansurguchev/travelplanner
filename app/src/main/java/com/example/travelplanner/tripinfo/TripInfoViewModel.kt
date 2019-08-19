package com.example.travelplanner.tripinfo

import android.app.Application
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.Action
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.data.PlannerInteractor
import com.example.travelplanner.base.routing.PlannerRouterHolder
import com.example.travelplanner.tripslist.TripItem
import com.example.travelplanner.tripslist.toItem
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TripInfoViewModel(application: Application, lifecycle: Lifecycle, private val tripId: Long)
    : AndroidViewModel(application), LifecycleObserver {

    var tripState = MutableLiveData<TripItem>()
    var actionsState = MutableLiveData<List<Item>>()

    @Inject
    internal lateinit var plannerInteractor: PlannerInteractor
    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    private var compositeDisposable = CompositeDisposable()

    init {
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        val tripDisposable =plannerInteractor
            .getTrip(tripId)
            .subscribe({ onTripLoaded(it) }, { it.printStackTrace() })
        compositeDisposable.add(tripDisposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        compositeDisposable.dispose()
    }

    fun onCreateActionClicked() {
        routerHolder.router?.goToActionCreation(tripId) { onActionCreated(it as Action) }
    }

    fun onBackPressed() {
        routerHolder.router?.goBack()
    }

    private fun onTripLoaded(trip: Trip) {
        tripState.postValue(trip.toItem())
        actionsState.postValue(trip.actions.toItems())
    }

    private fun onActionCreated(action: Action) {
        val addActionDisposable = plannerInteractor
            .addTripAction(tripId, action)
            .map { it.actions.toItems() }
            .subscribe({ actionsState.postValue(it) }, { it.printStackTrace() })
        compositeDisposable.add(addActionDisposable)
    }
}

class TripInfoViewModelFactory(private val application: Application, private val tripId: Long,
                               private val lifecycle: Lifecycle) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripInfoViewModel(application, lifecycle, tripId) as T
    }
}