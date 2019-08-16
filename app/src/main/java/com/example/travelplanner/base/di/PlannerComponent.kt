package com.example.travelplanner.base.di

import com.example.travelplanner.PlannerActivity
import com.example.travelplanner.actioncreate.ActionCreateViewModel
import com.example.travelplanner.tripcreate.TripCreateViewModel
import com.example.travelplanner.tripinfo.TripInfoViewModel
import com.example.travelplanner.tripslist.TripsListViewModel
import dagger.Component

@PlannerScope
@Component(modules = [PlannerModule::class])
interface PlannerComponent {
    fun inject(plannerActivity: PlannerActivity)
    fun inject(tripsListViewModel: TripsListViewModel)
    fun inject(tripsListViewModel: TripInfoViewModel)
    fun inject(actionCreateViewModel: ActionCreateViewModel)
    fun inject(tripCreateViewModel: TripCreateViewModel)
}
