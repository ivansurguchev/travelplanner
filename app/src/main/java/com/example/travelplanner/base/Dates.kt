package com.example.travelplanner.base

import org.joda.time.DateTime
import java.io.Serializable
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import org.joda.time.format.ISODateTimeFormat
import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

private const val FULL_MONTH_DATE_PATTERN = "d MMMM"
private const val DATE_TIME_PATTERN = "hh:mm"

fun DateTime.toReadableString(): String = this.toString(FULL_MONTH_DATE_PATTERN)

fun DateTime.toReadableTimeString(): String = this.toString(DATE_TIME_PATTERN)

class DateRange(val start: DateTime, val end: DateTime): Serializable {
    fun toReadableString(): String {
        val sameMonth = start.year == end.year && start.monthOfYear == end.monthOfYear
        val fromString: String =
            if (sameMonth) start.dayOfMonth.toString()
            else start.toString(FULL_MONTH_DATE_PATTERN)
        val toString = end.toString(FULL_MONTH_DATE_PATTERN)
        return String.format(RANGE_PATTERN, fromString, toString)
    }
}

class DateTimeSerializer : JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
    companion object {
        private val TIME_FORMAT = ISODateTimeFormat.dateTime()
    }

    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type,
                             jdc: JsonDeserializationContext): DateTime? {
        val dateAsString = je.asString
        return if (dateAsString.isEmpty()) null else TIME_FORMAT.parseDateTime(dateAsString)
    }

    override fun serialize(src: DateTime?, typeOfSrc: Type,
                           context: JsonSerializationContext): JsonElement {
        val retVal = if (src == null) "" else TIME_FORMAT.print(src)
        return JsonPrimitive(retVal)
    }
}