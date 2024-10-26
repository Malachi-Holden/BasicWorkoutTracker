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
    val dayIndex: Int?,
    val exercises: StateFlow<OrderedMap<String, Exercise>>,
    val setExercises: (OrderedMap<String, Exercise>) -> Unit
) {
    val exerciseState: Exercise?
        @Composable
        get() = exercises.collectAsState().value[exerciseKey.collectAsState().value]


    private val _editSetIndex = MutableStateFlow<Int?>(null)
    private val _copySetIndex = MutableStateFlow<Int?>(null)
    val showAddSet = MutableStateFlow(false)
    val showEditSet: Boolean
    @Composable
    get() = _editSetIndex.collectAsState().value != null

    val showCopySet: Boolean
    @Composable
    get() = _copySetIndex.collectAsState().value != null

    val workoutToUpdate: Workout?
    @Composable
    get() {
        val exerciseHistory = exerciseState?.history
        return bindNullable { exerciseHistory.bind()[dayIndex.bind()].sets[_editSetIndex.value.bind()] }
    }

    val workoutToCopy: Workout?
    @Composable
    get() {
        val exerciseHistory = exerciseState?.history
        return bindNullable { exerciseHistory.bind()[dayIndex.bind()].sets[_copySetIndex.value.bind()] }
    }

    fun addSet(
        set: Workout
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value.bind()].bind()
        val exerciseForDay = exercise.history[dayIndex.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets + set
                ))
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun showUpdateSet(index: Int) {
        _editSetIndex.value = index
    }
    fun closeUpdateSetPopup() {
        _editSetIndex.value = null
    }

    fun showCopySet(index: Int) {
        _copySetIndex.value = index
    }

    fun closeCopySetPopup() {
        _copySetIndex.value = null
    }

    fun removeSet(
        setIndex: Int
    ) = bindNullable {
        val exercise = exercises.value[exerciseKey.value].bind()
        val exerciseForDay = exercise.history[dayIndex.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets.removed(setIndex)
                ))
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    fun updateSet(newSet: Workout) = bindNullable {
        val setIndex = _editSetIndex.value.bind()
        val exercise = exercises.value[exerciseKey.value].bind()
        val exerciseForDay = exercise.history[dayIndex.bind()]
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.bind(), exerciseForDay.copy(
                    sets = exerciseForDay.sets.replaced(setIndex, newSet)
                ))
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises).also {
        _editSetIndex.value = null
    }
}