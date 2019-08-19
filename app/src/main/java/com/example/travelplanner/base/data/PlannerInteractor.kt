package com.example.travelplanner.base.data

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
        return tripsLoadedSubject
            .firstOrError()
            .map { it.values.toList().sortedBy { trip -> trip.startsAt.millis } }
            .doOnError { it.printStackTrace() }
    }

    fun saveTrip(trip: Trip): Single<List<Trip>> {
        return tripsLoadedSubject
            .firstOrError()
            .onErrorReturn { mutableMapOf() }
            .map {
                val id = trip.id
                it[id] = trip
                it
            }
            .doOnSuccess {
                storageManager.save(TRIPS_FILE_NAME, it)
                tripsLoadedSubject.onNext(it)
            }
            .doOnError { it.printStackTrace() }
            .map { it.values.toList().sortedBy { trip -> trip.startsAt.millis } }
    }

    fun getTrip(id: Long?): Single<Trip> {
        return tripsLoadedSubject
            .firstOrError()
            .map { it[id] ?: throw NoSuchElementException() }
            .doOnError { it.printStackTrace() }
    }

    fun addTripAction(tripId: Long, action: Action): Single<Trip> {
        return tripsLoadedSubject
            .firstOrError()
            .map { it[tripId]?.apply { actions.add(action) } ?: throw NoSuchElementException() }
            .doOnSuccess { saveTrip(it) }
    }
}

