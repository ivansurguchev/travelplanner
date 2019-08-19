package com.example.travelplanner

import android.app.Application
import com.example.travelplanner.base.di.PlannerComponent
import com.example.travelplanner.base.di.PlannerModule
import com.example.travelplanner.base.di.DaggerPlannerComponent
import net.danlew.android.joda.JodaTimeAndroid

class PlannerApplication : Application() {

    private lateinit var plannerComponent: PlannerComponent

    companion object {
        var instance: PlannerApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        init()
    }

    fun getPlannerComponent(): PlannerComponent = plannerComponent

    fun getContext() = applicationContext

    private fun init() {
        plannerComponent = DaggerPlannerComponent.builder()
            .plannerModule(PlannerModule(this))
            .build()
        JodaTimeAndroid.init(this)
    }
}
