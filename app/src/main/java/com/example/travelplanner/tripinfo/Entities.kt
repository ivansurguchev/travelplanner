package com.example.travelplanner.tripinfo

import com.example.travelplanner.base.Action
import com.example.travelplanner.base.toReadableString
import com.example.travelplanner.base.toReadableTimeString
import org.joda.time.DateTime

sealed class Item(val type: ItemType)

enum class ItemType { ACTION, DAY }

class DayItem(val dayText: String): Item(ItemType.DAY)

class ActionItem(val timeText: String, val primaryText: String, val secondaryText: String): Item(ItemType.ACTION)

fun List<Action>.toItems(): List<Item> {
    val result = mutableListOf<Item>()
    var lastDate: DateTime? = null
    for (action in this.sortedBy { it.startsAt.millis}) {
        if (lastDate == null || lastDate.dayOfMonth() != action.startsAt.dayOfMonth()) {
            result.add(DayItem(action.startsAt.toReadableString()))
        }
        result.add(
            ActionItem(
                action.startsAt.toReadableTimeString(),
                action.name,
                ""
            )
        )
        lastDate = action.startsAt
    }
    return result
}