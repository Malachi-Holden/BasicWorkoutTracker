package com.holden.basicworkouttracker.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.exercise.ExerciseViewModel
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.map
import com.holden.basicworkouttracker.util.swap
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class MainViewModel(
    initialGroups: OrderedMap<String, ExerciseGroup>,
    initialExercises: OrderedMap<String, Exercise>,
    val save: (OrderedMap<String, Exercise>) -> Unit
): ViewModel() {
    val groupsFlow = MutableStateFlow(initialGroups)
    val exercisesFlow = MutableStateFlow(initialExercises)

    val groupsAsState: OrderedMap<String, ExerciseGroup>
        @Composable
        get() = groupsFlow.collectAsState().value
    val exercisesAsState: OrderedMap<String, Exercise>
        @Composable
        get() = exercisesFlow.collectAsState().value

    val exerciseViewModel: ExerciseViewModel = ExerciseViewModel(
        MutableStateFlow(null),
        exercisesFlow,
        ::updateExercises
    )

    private val _showAddGroup = MutableStateFlow(false)
    val showAddGroup: Boolean
        @Composable
        get() = _showAddGroup.collectAsState().value

    private val _editingGroupId = MutableStateFlow<String?>(null)

    val editingGroupId: String?
        @Composable
        get() = _editingGroupId.collectAsState().value

    val editingGroup: ExerciseGroup?
        @Composable
        get() = editingGroupId?.let { groupsAsState[it] }

    private val _showAddExercise = MutableStateFlow(false)
    val showAddExercise: Boolean
        @Composable
        get() = _showAddExercise.collectAsState().value


    fun updateExercises(newExercises: OrderedMap<String, Exercise>) {
        exercisesFlow.value = newExercises
        save(newExercises)
    }

    fun updateGroups(newGroups: OrderedMap<String, ExerciseGroup>) {
        groupsFlow.value = newGroups
        // save
    }

    fun addGroupButtonClicked() {
        _showAddGroup.value = true
    }

    fun editGroupButtonClicked(id: String) {
        _editingGroupId.value = id
    }

    fun addExerciseButtonClicked() {
        _showAddExercise.value = true
    }

    fun onNewGroupPopupClosed() {
        _showAddGroup.value = false
    }

    fun onEditGroupPopupClosed() {
        _editingGroupId.value = null
    }
    fun onNewExercisePopupClosed() {
        _showAddExercise.value = false
    }

    fun onEditGroupComplete(newGroup: ExerciseGroup) {
        val id = _editingGroupId.value ?: return
        editGroup(id, newGroup)
    }

    fun addGroup(group: ExerciseGroup) {
        val uuid = UUID.randomUUID().toString()
        updateGroups(
            groupsFlow.value.append(uuid to group)
        )
        val dontShowSet = group.exerciseIds.toSet()
        updateExercises(
            exercisesFlow.value.map { key, exercise -> key to exercise.copy(showOnHomepage = exercise.showOnHomepage && key !in dontShowSet) }
        )
    }

    fun editGroup(uuid: String, newGroup: ExerciseGroup) {
        updateGroups(
            groupsFlow.value.replace(newGroup, uuid)
        )
        val dontShowSet = newGroup.exerciseIds.toSet()
        updateExercises(
            exercisesFlow.value.map { key, exercise -> key to exercise.copy(showOnHomepage = exercise.showOnHomepage && key !in dontShowSet) }
        )
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

    fun removeGroup(groupKey: String) {
        updateGroups(
            groupsFlow.value.remove(groupKey)
        )
    }

    fun removeExercise(exerciseKey: String) {
        updateExercises(
            exercisesFlow.value.remove(exerciseKey)
        )
    }

    fun swapGroups(startIndex: Int, endIndex: Int) {
        updateGroups(
            groupsFlow.value.swap(startIndex, endIndex)
        )
    }

    fun swapExercises(startIndex: Int, endIndex: Int) {
        updateExercises(
            exercisesFlow.value.swap(startIndex, endIndex)
        )
    }
}