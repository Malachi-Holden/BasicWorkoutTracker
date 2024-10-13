package com.holden.basicworkouttracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseForDay
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

    fun updateExercises(newExercises: OrderedMap<String, Exercise>) {
        exercisesFlow.value = newExercises
        saveExercises(newExercises)
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

    fun addDay(exerciseKey: String, day: ExerciseForDay) {
        val exercise = exercisesFlow.value[exerciseKey] ?: return
        updateExercises(
            exercisesFlow.value.replace(
                exercise.copy(history = listOf(day) + exercise.history),
                exerciseKey
            )
        )
    }

    fun removeDay(exerciseKey: String, dayIndex: Int) {
        val exercise = exercisesFlow.value[exerciseKey] ?: return
        updateExercises(
            exercisesFlow.value.replace(
                exercise.copy(
                    history = exercise.history.removed(dayIndex)
                ),
                exerciseKey
            )
        )
    }

    fun addSet(exerciseKey: String, day: Int, set: Workout) {
        val exercise = exercisesFlow.value[exerciseKey] ?: return
        val exerciseForDay = exercise.history[day]
        updateExercises(
            exercisesFlow.value.replace(
                exercise.copy(
                    history = exercise.history.replaced(day, exerciseForDay.copy(
                        sets = exerciseForDay.sets + set
                    ))
                ),
                exerciseKey
            )
        )
    }

    fun updateSet(exerciseKey: String, day: Int, setIndex: Int?, newSet: Workout) {
        setIndex ?: return
        val exercise = exercisesFlow.value[exerciseKey] ?: return
        val exerciseForDay = exercise.history[day]
        updateExercises(
            exercisesFlow.value.replace(
                exercise.copy(
                    history = exercise.history.replaced(day, exerciseForDay.copy(
                        sets = exerciseForDay.sets.replaced(setIndex, newSet)
                    ))
                ),
                exerciseKey
            )
        )
    }

    fun removeSet(exerciseKey: String, day: Int, setIndex: Int) {
        val exercise = exercisesFlow.value[exerciseKey] ?: return
        val exerciseForDay = exercise.history[day]
        updateExercises(
            exercisesFlow.value.replace(
                exercise.copy(
                    history = exercise.history.replaced(day, exerciseForDay.copy(
                        sets = exerciseForDay.sets.removed(setIndex)
                    ))
                ),
                exerciseKey
            )
        )
    }
}