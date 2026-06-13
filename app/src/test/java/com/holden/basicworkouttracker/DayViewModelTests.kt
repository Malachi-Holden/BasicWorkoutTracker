package com.holden.basicworkouttracker

import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseForDay
import com.holden.basicworkouttracker.exercise.Workout
import com.holden.basicworkouttracker.exercise.day.DayViewModel
import com.holden.basicworkouttracker.util.orderedMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import org.junit.Test
import org.junit.Assert.assertEquals

class DayViewModelTests {
    @Test
    fun `addSet correctly updates the exercises`(){
        val initialExercise = Exercise("hello", history = listOf(
            ExerciseForDay(date = LocalDate(2000, 1, 1), sets = listOf(
                Workout(reps = 1, weight = 10.0)
            ))
        ))
        var currentExercises = orderedMapOf("1" to initialExercise)
        val viewModel = DayViewModel(
            MutableStateFlow("1"),
            0,
            MutableStateFlow(currentExercises),
            { currentExercises = it },
            true
        )
        viewModel.addSet(Workout(reps = 2, weight = 10.0))
        assertEquals(orderedMapOf("1" to Exercise("hello", history = listOf(
            ExerciseForDay(date = LocalDate(2000, 1, 1), sets = listOf(
                Workout(reps = 1, weight = 10.0),
                Workout(reps = 2, weight = 10.0)
            ))
        ))), currentExercises)
    }
}