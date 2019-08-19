package com.example.travelplanner.base

import org.joda.time.DateTime
import java.io.Serializable

class Trip(
    val id: Long, val name: String = "", val actions: MutableList<Action>, val startsAt: DateTime, val endsAt: DateTime)
    : Serializable

class Action(
    val name: String, val startsAt: DateTime, val endsAt: DateTime? = null) : Serializable