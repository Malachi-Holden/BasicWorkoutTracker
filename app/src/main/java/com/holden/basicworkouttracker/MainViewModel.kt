package com.holden.basicworkouttracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseForDay
import com.holden.basicworkouttracker.exercise.ExerciseViewModel
import com.holden.basicworkouttracker.exercise.Workout
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.removed
import com.holden.basicworkouttracker.util.replaced
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class MainViewModel(
    initialExercises: OrderedMap<String, Exercise>,
    val saveExercises: (OrderedMap<String, Exercise>) -> Unit
): ViewModel() {
    val exercisesFlow = MutableStateFlow(initialExercises)

    val exercisesAsState: OrderedMap<String, Exercise>
        @Composable
        get() = exercisesFlow.collectAsState().value

    val exerciseViewModel: ExerciseViewModel = ExerciseViewModel(
        MutableStateFlow(null),
        exercisesFlow,
        ::updateExercises
    )

    val _deleteKey = MutableStateFlow<String?>(null)
    val deleteKey: String?
        @Composable
        get() = _deleteKey.collectAsState().value

    private val _showAddExercise = MutableStateFlow(false)
    val showAddExercise: Boolean
        @Composable
        get() = _showAddExercise.collectAsState().value

    fun rowClicked(key: String) {
        _deleteKey.value = if (_deleteKey.value == key) null else key
    }

    fun updateExercises(newExercises: OrderedMap<String, Exercise>) {
        exercisesFlow.value = newExercises
        saveExercises(newExercises)
    }

    fun addButtonClicked() {
        _showAddExercise.value = true
    }

    fun onPopupClosed() {
        _showAddExercise.value = false
    }

    /**
     * updates the data with the new exercise and returns the exercise key
     */
    fun addExercise(exercise: Exercise): String {
        val uuid = UUID.randomUUID().toString()
        updateExercises(
            exercisesFlow.value.append(uuid to exercise)
        )
        return uuid
    }

    fun removeExercise(exerciseKey: String) {
        updateExercises(
            exercisesFlow.value.remove(exerciseKey)
        )
    }
}