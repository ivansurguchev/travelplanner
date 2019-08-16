package com.example.travelplanner.tripslist

import android.app.Application
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.architecture.LoadingScreenState
import com.example.travelplanner.base.architecture.LoadingState
import com.example.travelplanner.base.architecture.update
import com.example.travelplanner.base.data.PlannerInteractor
import com.example.travelplanner.base.routing.PlannerRouterHolder
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TripsListViewModel(application: Application, lifecycle: Lifecycle)
    : AndroidViewModel(application), LifecycleObserver {

    var state = TripsListLiveData()

    @Inject
    internal lateinit var plannerInteractor: PlannerInteractor
    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    private var allTripsSubscription: Disposable? = null
    private var trips: MutableMap<Long?, Trip> = mutableMapOf()

    init {
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        allTripsSubscription = plannerInteractor.loadTrips()
            .doOnSubscribe { state.value =
                LoadingScreenState(LoadingState.LOADING)
            }
            .doOnSuccess { list -> trips = list.associateBy { it.id }.toMutableMap() }
            .onErrorReturn { if (it is NoSuchElementException) listOf() else throw(it) }
            .map { list -> list.sortedBy { it.endsAt.millis }.map { it.toItem() } }
            .subscribe(
                { state.value = LoadingScreenState(
                    LoadingState.LOADED,
                    it
                )
                },
                { state.value = LoadingScreenState(
                    LoadingState.ERROR,
                    null,
                    it.message
                )
                })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        allTripsSubscription?.dispose()
    }

    fun onCreateNewTripClick() {
        routerHolder.router?.goToTripCreation { onTripEdited(it as Trip) }
    }

    fun onTripClick(id: Long?) {
        val tripToGo = trips[id]
        if (tripToGo != null) routerHolder.router?.goToTrip(tripToGo.id)
    }

    private fun onTripEdited(trip: Trip) {
        plannerInteractor.saveTrip(trip)
        trips[trip.id] = trip
        state.value?.loadedData = trips.values.sortedByDescending { it.endsAt.millis}.map { it.toItem() }
        state.update()
    }
}

class TripsListViewModelFactory(private val application: Application, private val lifecycle: Lifecycle)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripsListViewModel(application, lifecycle) as T
    }
}

