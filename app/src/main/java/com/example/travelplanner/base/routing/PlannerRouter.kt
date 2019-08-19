package com.example.travelplanner.base.routing

interface PlannerRouter : Router {
    fun goToTripsList()

    fun goToTrip(tripId: Long)

    fun goToTripCreation(resultCallback: (Any?) -> Unit)

    fun goToActionCreation(tripId: Long, resultCallback: (Any?) -> Unit)

    fun provideResult(key: String, value: Any?)
}

class PlannerRouterHolder {
    val callbacks: MutableMap<String, (Any?) -> Unit> = mutableMapOf()
    var router: PlannerRouter? = null
}