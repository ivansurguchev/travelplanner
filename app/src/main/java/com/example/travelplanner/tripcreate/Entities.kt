package com.example.travelplanner.tripcreate

import androidx.annotation.StringRes
import com.example.travelplanner.R
import org.joda.time.DateTime

enum class TripCreateError(val condition: (CreateTripState?) -> Boolean, @StringRes val messageRes: Int) {
    NAME_NOT_SET
        ({ it?.name.isNullOrEmpty() }, R.string.error_trip_no_name),
    START_DATE_NOT_SET
        ({ it?.startDate == null }, R.string.error_trip_no_start_date),
    END_DATE_NOT_SET
        ({ it?.endDate == null }, R.string.error_trip_no_end_date),
    START_DATE_AFTER_END_DATE
        ({ it?.endDate?.isAfter(it.startDate) != true }, R.string.error_trip_start_date_after_end_date)
}

class CreateTripState(var name: String? = null, var startDate: DateTime? = null, var endDate: DateTime? = null, var error: String? = null)