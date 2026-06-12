package com.holden.basicworkouttracker.home

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holden.basicworkouttracker.di.Persistence
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.exercise.ExerciseViewModel
import com.holden.basicworkouttracker.persistence.BWTData
import com.holden.basicworkouttracker.persistence.LOCAL_PLATES
import com.holden.basicworkouttracker.persistence.savePlates
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.bindNullable
import com.holden.basicworkouttracker.util.map
import com.holden.basicworkouttracker.util.swap
import com.holden.basicworkouttracker.util.toOrderedMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    persistence: Persistence,
): ViewModel() {
    val saveExercises = persistence.saveExercises
    val saveGroups = persistence.saveGroups
    val _groupsFlow = MutableStateFlow(persistence.initialGroups)
    val _exercisesFlow = MutableStateFlow(persistence.initialExercises)

    val _editMode = MutableStateFlow(false)
    val editMode: Boolean
        @Composable
        get() = _editMode.collectAsState().value

    val groupsAsState: OrderedMap<String, ExerciseGroup>
        @Composable
        get() = _groupsFlow.collectAsState().value
    val exercisesAsState: OrderedMap<String, Exercise>
        @Composable
        get() = _exercisesFlow.collectAsState().value

    val exerciseViewModel: ExerciseViewModel = ExerciseViewModel(
        MutableStateFlow(null),
        _exercisesFlow,
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

    private val _deleteingGroupId = MutableStateFlow<String?>(null)
    val deletingGroupId: String?
        @Composable
        get() = _deleteingGroupId.collectAsState().value

    val deletingGroup: ExerciseGroup?
        @Composable
        get() = deletingGroupId?.let { groupsAsState[it] }

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
        _exercisesFlow.value = newExercises
        saveExercises(viewModelScope, newExercises)
    }

    private fun updateGroups(newGroups: OrderedMap<String, ExerciseGroup>) {
        _groupsFlow.value = newGroups
        saveGroups(viewModelScope, newGroups)
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

    fun deleteGroupButtonClicked(id: String) {
        _deleteingGroupId.value = id
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

    fun onDeleteGroupPopopClosed() {
        _deleteingGroupId.value = null
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
            _groupsFlow.value.append(uuid to group)
        )
        updateExtraExercises()
    }

    fun toggleGroupCollapsed(uuid: String) = bindNullable {
        val group = _groupsFlow.value[uuid].bind()
        editGroup(uuid, group.copy(
            collapsed = !group.collapsed
        ))
    }

    fun editGroup(uuid: String, newGroup: ExerciseGroup) {
        updateGroups(
            _groupsFlow.value.replace(newGroup, uuid)
        )
        updateExtraExercises()
    }

    fun editExercise(uuid: String, newExercise: Exercise) {
        updateExercises(
            _exercisesFlow.value.replace(newExercise, uuid)
        )
    }

    fun addExercise(exercise: Exercise) {
        val uuid = UUID.randomUUID().toString()
        updateExercises(
            _exercisesFlow.value.append(uuid to exercise)
        )
    }

    fun removeGroup(groupKey: String) {
        updateGroups(
            _groupsFlow.value.remove(groupKey)
        )
        updateExtraExercises()
    }

    fun updateExtraExercises() {
        val noShowSet = _groupsFlow.value.values.flatMap { it.exerciseIds }.toSet()
        updateExercises(
            _exercisesFlow.value.map { key, exercise ->
                key to exercise.copy(showOnHomepage = key !in noShowSet)
            }
        )
    }

    fun removeExercise(exerciseKey: String) {
        updateExercises(
            _exercisesFlow.value.remove(exerciseKey)
        )
    }

    fun swapGroups(startIndex: Int, endIndex: Int) {
        updateGroups(
            _groupsFlow.value.swap(startIndex, endIndex)
        )
    }

    fun swapExercises(startIndex: Int, endIndex: Int) {
        updateExercises(
            _exercisesFlow.value.swap(startIndex, endIndex)
        )
    }
}