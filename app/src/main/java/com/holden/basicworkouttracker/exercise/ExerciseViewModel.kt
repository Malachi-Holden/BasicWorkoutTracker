package com.holden.basicworkouttracker.exercise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.holden.basicworkouttracker.exercise.day.DayViewModel
import com.holden.basicworkouttracker.util.OrderedMap
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

    val dayViewModel = DayViewModel(
        exerciseKey,
        MutableStateFlow(null),
        exercises,
        setExercises
    )
    val exerciseState: Exercise?
        @Composable
        get() = exercises.collectAsState().value[exerciseKey.collectAsState().value]

    val deleteIndex: Int?
        @Composable
        get() = _deleteIndex.collectAsState().value

    fun addDay(day: ExerciseForDay) {
        setExercises(addDay(
            exercises.value,
            exerciseKey.value ?: return,
            day
        ))
    }

    fun removeDay(dayIndex: Int) {
        setExercises(removeDay(
            exercises.value,
            exerciseKey.value ?: return,
            dayIndex
        ))
        _deleteIndex.value = null
    }

    fun addDay(
        exercises: OrderedMap<String, Exercise>,
        exerciseKey: String,
        day: ExerciseForDay
    ): OrderedMap<String, Exercise> {
        val exercise = exercises[exerciseKey] ?: return exercises
        return exercises.replace(
            exercise.copy(history = listOf(day) + exercise.history),
            exerciseKey
        )
    }

    fun removeDay(
        exercises: OrderedMap<String, Exercise>,
        exerciseKey: String,
        dayIndex: Int
    ): OrderedMap<String, Exercise> {
        val exercise = exercises[exerciseKey] ?: return exercises
        return exercises.replace(
            exercise.copy(
                history = exercise.history.removed(dayIndex)
            ),
            exerciseKey
        )
    }



    fun dayClicked(dayIndex: Int) {
        val showDelete = _deleteIndex.value == dayIndex
        _deleteIndex.value = if (showDelete) null else dayIndex
    }
}