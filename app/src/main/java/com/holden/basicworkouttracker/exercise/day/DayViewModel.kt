package com.holden.basicworkouttracker.exercise.day

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.Workout
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.bindNullable
import com.holden.basicworkouttracker.util.removed
import com.holden.basicworkouttracker.util.replaced
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DayViewModel(
    val exerciseKey: MutableStateFlow<String?>,
    val dayIndex: MutableStateFlow<Int?>,
    val exercises: StateFlow<OrderedMap<String, Exercise>>,
    val setExercises: (OrderedMap<String, Exercise>) -> Unit
) {
    val exerciseState: Exercise?
        @Composable
        get() = exercises.collectAsState().value[exerciseKey.collectAsState().value]

    val dayIndexState: Int?
        @Composable
        get() = dayIndex.collectAsState().value

    fun addSet(
        set: Workout
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value.bind()].bind()
        val exerciseForDay = exercise.history[dayIndex.value.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.value.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets + set
                ))
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun removeSet(
        setIndex: Int
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value].bind()
        val exerciseForDay = exercise.history[dayIndex.value.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.value.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets.removed(setIndex)
                ))
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun updateSet(setIndex: Int?, newSet: Workout) = bindNullable{
        val exercise = exercises.value[exerciseKey.value].bind()
        val exerciseForDay = exercise.history[dayIndex.value.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.value.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets.replaced(setIndex.bind(), newSet)
                ))
            ),
            exerciseKey.value.bind()
        )

    }?.let(setExercises)
}