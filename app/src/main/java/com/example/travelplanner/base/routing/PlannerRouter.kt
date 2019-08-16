package com.example.travelplanner.base.routing

interface PlannerRouter : Router {
    fun goToTripsList()

    fun goToTrip(tripId: Long)

    fun goToTripCreation(resultCallback: (Any?) -> Unit)

    fun goToActionCreation(tripId: Long, resultCallback: (Any?) -> Unit)

    fun provideResult(key: String, value: Any?)
}

class PlannerRouterHolder {

    var router: PlannerRouter? = null
        private set

    fun registerRouter(router: PlannerRouter) {
        this.router = router
    }

    fun unregisterRouter() {
        this.router = null
    }
}