package com.example.travelplanner.actioncreate

import androidx.annotation.StringRes
import com.example.travelplanner.R
import org.joda.time.DateTime

class ActionCreateState(var name: String? = null, var date: DateTime? = null,
                        var startHours: Int? = null, var startMinutes: Int? = null,
                        var endHours: Int? = null, var endMinutes: Int? = null,
                        var error: String? = null)

enum class ActionCreateError(val condition: (ActionCreateState?) -> Boolean, @StringRes val messageRes: Int) {
    NAME_NOT_SET
        ({ it?.name.isNullOrEmpty() }, R.string.error_trip_no_name),
    DATE_NOT_SET
        ({ it?.date == null }, R.string.error_action_no_date),
    START_TIME_NOT_SET
        ({ it?.startHours == null || it.startMinutes == null }, R.string.error_action_no_start_time),
    START_TIME_AFTER_END_TIME
        ({ it?.startTimeAfterEndTime() ?: false }, R.string.error_action_start_time_after_end_time)
}

private fun ActionCreateState.startTimeAfterEndTime(): Boolean {
    return if (date != null && startHours != null && startMinutes != null && endHours != null && endMinutes != null) {
        val startDateTime = date!!.withHourOfDay(startHours!!).withMinuteOfHour(startMinutes!!)
        val endDateTime = date!!.withHourOfDay(endHours!!).withMinuteOfHour(endMinutes!!)
        startDateTime.isAfter(endDateTime)
    } else {
        false
    }
}