package com.holden.basicworkouttracker.exercise

import android.annotation.SuppressLint
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ExerciseGroup(
    val title: String,
    val notes: String = "",
    val exerciseIds: List<String>,
    val collapsed: Boolean = false
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Exercise(
    val title: String,
    val notes: String = "",
    val history: List<ExerciseForDay>,
    val showOnHomepage: Boolean = true
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ExerciseForDay(
    val notes: String = "",
    val date: LocalDate?,
    val sets: List<Workout>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Workout(
    val notes: String = "",
    val reps: Int,
    val weight: Double
)

fun currentDate() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date