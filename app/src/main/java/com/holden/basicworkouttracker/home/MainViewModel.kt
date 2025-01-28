package com.holden.basicworkouttracker.home

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.exercise.ExerciseViewModel
import com.holden.basicworkouttracker.persistence.BWTData
import com.holden.basicworkouttracker.persistence.LOCAL_PLATES
import com.holden.basicworkouttracker.persistence.savePlates
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.map
import com.holden.basicworkouttracker.util.swap
import com.holden.basicworkouttracker.util.toOrderedMap
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class MainViewModel(
    initialGroups: OrderedMap<String, ExerciseGroup>,
    initialExercises: OrderedMap<String, Exercise>,
    val saveExercises: (OrderedMap<String, Exercise>) -> Unit,
    val saveGroups: (OrderedMap<String, ExerciseGroup>) -> Unit
): ViewModel() {
    val groupsFlow = MutableStateFlow(initialGroups)
    val exercisesFlow = MutableStateFlow(initialExercises)

    val _editMode = MutableStateFlow(false)
    val editMode: Boolean
        @Composable
        get() = _editMode.collectAsState().value

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

    val editingGroup: ExerciseGroup?
        @Composable
        get() = _editingGroupId
            .collectAsState()
            .value
            ?.let { groupsAsState[it] }

    private val _editingExerciseId = MutableStateFlow<String?>(null)

    val editingExercise: Exercise?
        @Composable
        get() = _editingExerciseId
            .collectAsState()
            .value
            ?.let { exercisesAsState[it] }

    private val _showAddExercise = MutableStateFlow(false)
    val showAddExercise: Boolean
        @Composable
        get() = _showAddExercise.collectAsState().value

    fun loadFromBWTData(
        context: Context,
        data: BWTData
    ) {
        updateExercises(data.exercises.toOrderedMap())
        updateGroups(data.groups.toOrderedMap())
        val (bar, weights) = data.plates
        context.savePlates(LOCAL_PLATES, weights, bar)
    }

    private fun updateExercises(newExercises: OrderedMap<String, Exercise>) {
        exercisesFlow.value = newExercises
        saveExercises(newExercises)
    }

    private fun updateGroups(newGroups: OrderedMap<String, ExerciseGroup>) {
        groupsFlow.value = newGroups
        saveGroups(newGroups)
    }

    fun editButtonClicked() {
        _editMode.value = !_editMode.value
    }

    fun addGroupButtonClicked() {
        _showAddGroup.value = true
    }

    fun editGroupButtonClicked(id: String) {
        _editingGroupId.value = id
    }

    fun editExerciseButtonClicked(id: String) {
        _editingExerciseId.value = id
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

    fun onEditExercisePopupClosed() {
        _editingExerciseId.value = null
    }
    fun onNewExercisePopupClosed() {
        _showAddExercise.value = false
    }

    fun onEditGroupComplete(newGroup: ExerciseGroup) {
        val id = _editingGroupId.value ?: return
        editGroup(id, newGroup)
    }

    fun onEditExerciseComplete(newExercise: Exercise) {
        val id = _editingExerciseId.value ?: return
        editExercise(id, newExercise)
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

    private fun editGroup(uuid: String, newGroup: ExerciseGroup) {
        updateGroups(
            groupsFlow.value.replace(newGroup, uuid)
        )
        val dontShowSet = newGroup.exerciseIds.toSet()
        updateExercises(
            exercisesFlow.value.map { key, exercise -> key to exercise.copy(showOnHomepage = exercise.showOnHomepage && key !in dontShowSet) }
        )
    }

    fun editExercise(uuid: String, newExercise: Exercise) {
        updateExercises(
            exercisesFlow.value.replace(newExercise, uuid)
        )
    }

    fun addExercise(exercise: Exercise) {
        val uuid = UUID.randomUUID().toString()
        updateExercises(
            exercisesFlow.value.append(uuid to exercise)
        )
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