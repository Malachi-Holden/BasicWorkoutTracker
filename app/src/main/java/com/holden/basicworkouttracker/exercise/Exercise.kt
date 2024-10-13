package com.holden.basicworkouttracker.exercise

import com.holden.basicworkouttracker.util.orderedMapOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class Exercise(val title: String, val history: List<ExerciseForDay>)

@Serializable
data class ExerciseForDay(val date: LocalDate?, val sets: List<Workout>)

@Serializable
data class Workout(val reps: Int, val weight: Double)

fun currentDate() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

val testExercises = orderedMapOf(
    "1" to Exercise("Squat", listOf(
        ExerciseForDay(
            currentDate() - DatePeriod(days = 2), listOf(
            Workout(10, 155.0),
            Workout(10, 155.0),
            Workout(10, 155.0),
            Workout(10, 155.0),
        )),
        ExerciseForDay(
            currentDate() - DatePeriod(days = 1), listOf(
            Workout(10, 165.0),
            Workout(10, 165.0),
            Workout(10, 165.0),
            Workout(10, 165.0),
        )),
        ExerciseForDay(
            currentDate(), listOf(
            Workout(10, 175.0),
            Workout(10, 175.0),
            Workout(10, 175.0),
            Workout(10, 175.0),
        )),
    )),
    "2" to Exercise("Bench", listOf(
        ExerciseForDay(
            currentDate() - DatePeriod(days = 2), listOf(
            Workout(10, 155.0),
            Workout(10, 155.0),
            Workout(10, 155.0),
            Workout(10, 155.0),
        )),
        ExerciseForDay(
            currentDate() - DatePeriod(days = 1), listOf(
            Workout(10, 165.0),
            Workout(10, 165.0),
            Workout(10, 165.0),
            Workout(10, 165.0),
        )),
        ExerciseForDay(
            currentDate(), listOf(
            Workout(10, 175.0),
            Workout(10, 175.0),
            Workout(10, 175.0),
            Workout(10, 175.0),
        )),
    )),
)