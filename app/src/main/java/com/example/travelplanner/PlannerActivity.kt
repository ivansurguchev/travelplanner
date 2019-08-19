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

    @Inject
    internal lateinit var routerHolder: PlannerRouterHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBar()
        PlannerApplication.instance?.getPlannerComponent()?.inject(this)
        routerHolder.router = this
        if (savedInstanceState == null) goToTripsList()
    }

    override fun onDestroy() {
        super.onDestroy()
        routerHolder.router = null
    }

    override fun onBackPressed() {
        goBack()
    }

    override fun goToTripsList() {
        showFragment(TripsListFragment())
    }

    override fun goToTrip(tripId: Long) {
        showFragment(TripInfoFragment.newInstance(tripId))
    }

    override fun goToTripCreation(resultCallback: (Any?) -> Unit) {
        showFragment(TripCreateFragment())
        routerHolder.callbacks[TRIP_CREATION_KEY] = resultCallback
    }

    override fun goToActionCreation(tripId: Long, resultCallback: (Any?) -> Unit) {
        showFragment(ActionCreateFragment.newInstance(tripId))
        routerHolder.callbacks[ACTION_CREATION_KEY] = resultCallback
    }

    override fun goBack() {
        if (supportFragmentManager.backStackEntryCount == 1) finish()
        else supportFragmentManager.popBackStack()
    }

    override fun provideResult(key: String, value: Any?) {
        routerHolder.callbacks[key]?.invoke(value)
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
