package com.example.travelplanner.tripslist

import androidx.lifecycle.MutableLiveData
import com.example.travelplanner.base.DateRange
import com.example.travelplanner.base.Trip
import com.example.travelplanner.base.architecture.LoadingScreenState

class TripItem(val objectId: Long?, val name: String, val primaryText: String, val secondaryText: String)

fun Trip.toItem() =
    TripItem(id, name, DateRange(startsAt, endsAt).toReadableString(), "")

typealias TripsListLiveData = MutableLiveData<LoadingScreenState<List<TripItem>, String>>