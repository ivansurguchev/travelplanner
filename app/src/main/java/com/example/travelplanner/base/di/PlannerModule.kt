package com.example.travelplanner.base.di

import android.content.Context
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.base.data.PlannerInteractor
import com.example.travelplanner.base.DateTimeSerializer
import com.example.travelplanner.base.data.StorageManager
import com.example.travelplanner.base.routing.PlannerRouterHolder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime

@Module
class PlannerModule(private val application: PlannerApplication) {
    @PlannerScope
    @Provides
    internal fun provideTripInteractor(storageManager: StorageManager): PlannerInteractor {
        return PlannerInteractor(storageManager)
    }

    @PlannerScope
    @Provides
    internal fun provideStorageMangaer(context: Context, gson: Gson): StorageManager {
        return StorageManager(context, gson)
    }

    @PlannerScope
    @Provides
    internal fun provideContext(): Context {
        return application.applicationContext
    }

    @PlannerScope
    @Provides
    internal fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .enableComplexMapKeySerialization()
            .setLenient()
            .create()
    }

    @PlannerScope
    @Provides
    internal fun providePlannerRouterHolder(): PlannerRouterHolder {
        return PlannerRouterHolder()
    }
}
