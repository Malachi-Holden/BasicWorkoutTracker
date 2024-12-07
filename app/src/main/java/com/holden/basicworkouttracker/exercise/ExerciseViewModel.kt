package com.holden.basicworkouttracker.exercise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.holden.basicworkouttracker.exercise.day.DayViewModel
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.bindNullable
import com.holden.basicworkouttracker.util.removed
import com.holden.basicworkouttracker.util.replaced
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExerciseViewModel(
    val exerciseKey: MutableStateFlow<String?>,
    private val exercises: StateFlow<OrderedMap<String, Exercise>>,
    private val setExercises: (OrderedMap<String, Exercise>) -> Unit
) {

    private val _deleteIndex: MutableStateFlow<Int?> = MutableStateFlow(null)

    var showNewWorkoutOnNavigate = false
    val exerciseState: Exercise?
        @Composable
        get() = exercises.collectAsState().value[exerciseKey.collectAsState().value]

    val deleteIndex: Int?
        @Composable
        get() = _deleteIndex.collectAsState().value

    fun dayViewModel(dayIndex: Int?) = DayViewModel(
        exerciseKey,
        dayIndex,
        exercises,
        setExercises,
        showNewWorkoutOnNavigate
    )
    fun addDay(
        day: ExerciseForDay
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value].bind()
        exercises.value.replace(
            exercise.copy(history = listOf(day) + exercise.history),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun removeDay(
        dayIndex: Int
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value].bind()
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.removed(dayIndex)
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun dayClicked(dayIndex: Int) {
        val showDelete = _deleteIndex.value == dayIndex
        _deleteIndex.value = if (showDelete) null else dayIndex
    }

    fun updateNotes(newNotes: String) = bindNullable {
        val exercise = exercises.value[exerciseKey.value].bind()
        exercises.value.replace(
            exercise.copy(notes = newNotes),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)
}