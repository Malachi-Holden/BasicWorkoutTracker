package com.holden.basicworkouttracker.exercise.day

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.holden.basicworkouttracker.LOCAL_PLATES
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseForDay
import com.holden.basicworkouttracker.exercise.Workout
import com.holden.basicworkouttracker.loadPlates
import com.holden.basicworkouttracker.util.NullableScope
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.bindNullable
import com.holden.basicworkouttracker.util.removed
import com.holden.basicworkouttracker.util.replaced
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DayViewModel(
    val exerciseKey: MutableStateFlow<String?>,
    val dayIndex: Int?,
    val exercises: StateFlow<OrderedMap<String, Exercise>>,
    val setExercises: (OrderedMap<String, Exercise>) -> Unit,
    val showNewWorkout: Boolean
) {
    val exerciseState: Exercise?
        @Composable
        get() = exercises.collectAsState().value[exerciseKey.collectAsState().value]


    private val _editSetIndex = MutableStateFlow<Int?>(null)
    private val _copySetIndex = MutableStateFlow<Int?>(null)
    val showAddSet = MutableStateFlow(showNewWorkout)
    private val _weightForCalculator = MutableStateFlow<Double?>(null)
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

    val weightForCalculator: Double?
    @Composable
    get() = _weightForCalculator.collectAsState().value

    val showCalculator: Boolean
    @Composable
    get() = weightForCalculator != null

    val _showCalendar = MutableStateFlow(false)

    val showCalendar: Boolean
        @Composable
        get() = _showCalendar.collectAsState().value

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

    fun onShowCalculator(weight: Double?) {
        _weightForCalculator.value = weight
    }
    fun hideCalculator() {
        _weightForCalculator.value = null
    }

    fun showCalendar() {
        _showCalendar.value = true
    }

    fun hideCalendar() {
        _showCalendar.value = false
    }

    private val NullableScope.key
        get() = exerciseKey.value.bind()
    private val NullableScope.exercise
        get() = exercises.value[key].bind()
    private val NullableScope.exerciseForDay
        get() = exercise.history[dayIndex.bind()]

    fun onDateUpdated(newDate: Instant) = bindNullable {
        updateExerciseForDay(
            exerciseForDay.copy(date = newDate.toLocalDateTime(TimeZone.UTC).date)
        )
        hideCalendar()
    }

    fun removeSet(
        setIndex: Int
    ) = bindNullable {
        updateExerciseForDay(
            exerciseForDay.copy(
                sets = exerciseForDay.sets.removed(setIndex)
            )
        )
    }

    fun updateSet(newSet: Workout) = bindNullable {
        val setIndex = _editSetIndex.value.bind()
        updateExerciseForDay(exerciseForDay.copy(
            sets = exerciseForDay.sets.replaced(setIndex, newSet)
        ))
        _editSetIndex.value = null
    }

    private fun updateExerciseForDay(newDay: ExerciseForDay) = bindNullable {
        exercises.value.replace(
            exercise.copy(
                history = exercise.history.replaced(dayIndex.bind(), newDay)
            ),
            exerciseKey.value.bind()
        )
    }?.let(setExercises)

    @Composable
    fun loadPersistedPlates() = LocalContext.current.loadPlates(LOCAL_PLATES)
        ?: (45.0 to listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5))
}