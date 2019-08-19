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
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TripsListViewModel(application: Application, lifecycle: Lifecycle) : AndroidViewModel(application), LifecycleObserver {

    var state = TripsListLiveData()

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
        val allTripsDisposable = plannerInteractor.loadTrips()
            .doOnSubscribe { state.value =
                LoadingScreenState(LoadingState.LOADING)
            }
            .onErrorReturn { if (it is NoSuchElementException) listOf() else throw(it) }
            .map { it.toItems() }
            .subscribe(
                { state.value = LoadingScreenState(LoadingState.LOADED, it) },
                { state.value = LoadingScreenState(LoadingState.ERROR, null, it.message) })
        compositeDisposable.add(allTripsDisposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        compositeDisposable.dispose()
    }

    fun onCreateNewTripClick() {
        routerHolder.router?.goToTripCreation { onTripEdited(it as Trip) }
    }

    fun onTripClick(id: Long) {
        val tripClickDisposable = plannerInteractor
            .getTrip(id)
            .subscribe({ routerHolder.router?.goToTrip(id) }, { it.printStackTrace()})
        compositeDisposable.add(tripClickDisposable)
    }

    private fun onTripEdited(trip: Trip) {
        val tripEditDisposable = plannerInteractor
            .saveTrip(trip)
            .map { it.toItems() }
            .subscribe({
                state.value?.loadedData = it
                state.update()
            }, {
                it.printStackTrace()
            })
        compositeDisposable.add(tripEditDisposable)
    }
}

private fun List<Trip>.toItems() = sortedBy { it.startsAt.millis }.map { it.toItem() }

class TripsListViewModelFactory(private val application: Application, private val lifecycle: Lifecycle)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripsListViewModel(application, lifecycle) as T
    }
}


