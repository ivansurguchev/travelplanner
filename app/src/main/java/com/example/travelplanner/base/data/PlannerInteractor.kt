package com.example.travelplanner.base.data

import android.annotation.SuppressLint
import com.example.travelplanner.base.Action
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.applySchedulers
import com.example.travelplanner.base.justSubscribe
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

private const val TRIPS_FILE_NAME = "trips.json"

class PlannerInteractor(private val storageManager: StorageManager) {

    private val tripsLoadedSubject = BehaviorSubject.create<MutableMap<Long?, Trip>>()

    init {
        val tripsMapType = object : TypeToken<Map<Long?, Trip>>() {}.type
        storageManager
            .load<MutableMap<Long?, Trip>>(TRIPS_FILE_NAME, tripsMapType)
            .applySchedulers()
            .doOnSuccess { tripsLoadedSubject.onNext(it) }
            .doOnError { tripsLoadedSubject.onError(it) }
            .justSubscribe()
    }

    fun loadTrips(): Single<List<Trip>> {
        return tripsLoadedSubject.firstOrError().map { it.values.toList() }.doOnError { it.printStackTrace() }
    }

    @SuppressLint("CheckResult")
    fun saveTrip(trip: Trip) {
        tripsLoadedSubject
            .firstOrError()
            .map {
                val id = trip.id
                it[id] = trip
                it
            }
            .subscribe(
                {
                    storageManager.save(TRIPS_FILE_NAME, it)
                    tripsLoadedSubject.onNext(it)
                },
                {
                    it.printStackTrace()
                })
    }

    fun getTrip(id: Long?): Single<Trip> {
        return tripsLoadedSubject
            .firstOrError()
            .map { it[id] ?: throw NoSuchElementException() }
            .doOnError { it.printStackTrace() }
    }

    @SuppressLint("CheckResult")
    fun addTripAction(tripId: Long, action: Action): Single<Trip> {
        return tripsLoadedSubject
            .firstOrError()
            .map { it[tripId]?.apply { actions.add(action) } ?: throw NoSuchElementException() }
            .doOnSuccess { saveTrip(it) }
    }
}

