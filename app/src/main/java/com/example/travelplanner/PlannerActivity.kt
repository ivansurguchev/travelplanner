package com.example.travelplanner

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.travelplanner.actioncreate.ActionCreateFragment
import com.example.travelplanner.base.routing.ACTION_CREATION_KEY
import com.example.travelplanner.base.routing.PlannerRouter
import com.example.travelplanner.base.routing.PlannerRouterHolder
import com.example.travelplanner.base.routing.TRIP_CREATION_KEY
import com.example.travelplanner.tripcreate.TripCreateFragment
import com.example.travelplanner.tripinfo.TripInfoFragment
import com.example.travelplanner.tripslist.TripsListFragment
import javax.inject.Inject

class PlannerActivity : AppCompatActivity(), PlannerRouter {

    private val callbacks: MutableMap<String, (Any?) -> Unit> = mutableMapOf()

    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBar()
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        routerHolder.registerRouter(this)
        if (savedInstanceState == null) goToTripsList()
    }

    override fun onDestroy() {
        super.onDestroy()
        routerHolder.unregisterRouter()
    }

    override fun goToTripsList() {
        showFragment(TripsListFragment())
    }

    override fun goToTrip(tripId: Long) {
        showFragment(TripInfoFragment.newInstance(tripId))
    }

    override fun goToTripCreation(resultCallback: (Any?) -> Unit) {
        showFragment(TripCreateFragment())
        callbacks[TRIP_CREATION_KEY] = resultCallback
    }

    override fun goToActionCreation(tripId: Long, resultCallback: (Any?) -> Unit) {
        showFragment(ActionCreateFragment.newInstance(tripId))
        callbacks[ACTION_CREATION_KEY] = resultCallback
    }

    override fun goBack() {
        if (supportFragmentManager.backStackEntryCount == 1) finish()
        else supportFragmentManager.popBackStack()
    }

    override fun provideResult(key: String, value: Any?) {
        callbacks[key]?.invoke(value)
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBar() {
        window.statusBarColor = Color.WHITE
    }
}
