package com.holden.basicworkouttracker

import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseForDay
import com.holden.basicworkouttracker.exercise.ExerciseViewModel
import com.holden.basicworkouttracker.util.orderedMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import org.junit.Test
import org.junit.Assert.assertEquals

class ExerciseViewModelTests {
    @Test
    fun `addDay sets the exercises to have a new day`() {
        val initialExercise = Exercise("hello", history = listOf(
            ExerciseForDay(date = LocalDate(2000, 1, 1), sets = listOf())
        ))
        var currentExercises = orderedMapOf("1" to initialExercise)
        val viewModel = ExerciseViewModel(
            MutableStateFlow("1"),
            MutableStateFlow(orderedMapOf("1" to initialExercise)),
            { currentExercises = it}
        )
        viewModel.addDay(ExerciseForDay(date = LocalDate(2000, 1, 2), sets = listOf()))
        assertEquals(orderedMapOf("1" to Exercise("hello", history = listOf(
            ExerciseForDay(date = LocalDate(2000, 1, 2), sets = listOf()),
            ExerciseForDay(date = LocalDate(2000, 1, 1), sets = listOf()),
        ))), currentExercises)
    }
}