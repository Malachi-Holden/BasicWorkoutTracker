package com.holden.basicworkouttracker.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.R
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.persistence.buildLoadBWTFromSaveAction
import com.holden.basicworkouttracker.persistence.buildSaveBWTAction
import com.holden.basicworkouttracker.ui.theme.DefaultButton
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.Side
import com.holden.basicworkouttracker.util.items
import com.holden.basicworkouttracker.util.singleEdge

@Composable
fun HomePage(
    mainViewModel: MainViewModel,
    showExercise: (String) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)) {
        val exercises = mainViewModel.exercisesAsState
        val groups = mainViewModel.groupsAsState
        Column {
            EditableExerciseList(
                modifier = Modifier.weight(1f),
                groups = groups,
                exercises = exercises,
                mainViewModel = mainViewModel,
                showExercise = showExercise
            )
            EditButtonsRow(mainViewModel = mainViewModel)
        }

        AddGroupPopup(
            showPopup = mainViewModel.showAddGroup,
            exercises = exercises,
            onGroupCreated = mainViewModel::addGroup,
            onPopupClosed = mainViewModel::onNewGroupPopupClosed
        )
        EditGroupPopup(
            initialGroup = mainViewModel.editingGroup,
            showPopup = mainViewModel.editingGroup != null,
            exercises = exercises,
            headerText = stringResource(id = R.string.edit_exercise_group),
            doneButtonText = stringResource(id = R.string.save),
            onFinishedEditing = mainViewModel::onEditGroupComplete,
            onPopupClosed = mainViewModel::onEditGroupPopupClosed
        )
        AddExercisePopup(
            showPopup = mainViewModel.showAddExercise,
            onExerciseCreated = mainViewModel::addExercise,
            onPopupClosed = mainViewModel::onNewExercisePopupClosed
        )
        EditExercisePopup(
            headerText = stringResource(id = R.string.edit_exercise),
            doneText = stringResource(id = R.string.save),
            initialExercise = mainViewModel.editingExercise,
            showPopup = mainViewModel.editingExercise != null,
            onExerciseUpdated = mainViewModel::onEditExerciseComplete,
            onPopupClosed = mainViewModel::onEditExercisePopupClosed
        )
    }
}

@Composable
private fun EditButtonsRow(
    mainViewModel: MainViewModel
) {
    Row {
        Button(
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background
            ),
            onClick = mainViewModel::editButtonClicked
        ) {
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                text = if (mainViewModel.editMode) {
                    stringResource(id = R.string.end_editing)
                } else {
                    stringResource(id = R.string.edit)
                }
            )
        }
        if (mainViewModel.editMode) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background
                ),
                onClick = mainViewModel::addExerciseButtonClicked
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.add_exercise)
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background
                ),
                onClick = mainViewModel::addGroupButtonClicked
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.add_group)
                )
            }
        } else {
            val saveAction = buildSaveBWTAction()
            Button(
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background
                ),
                onClick = saveAction
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.export_data)
                )
            }
            val context = LocalContext.current
            val loadAction = buildLoadBWTFromSaveAction { data ->
                if (data != null) {
                    mainViewModel.loadFromBWTData(context, data)
                } else {
                    Toast.makeText(
                        context,
                        R.string.corrupt_data,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.background
                ),
                onClick = loadAction
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.import_data)
                )
            }
        }

    }
}

@Composable
fun EditableExerciseList(
    modifier: Modifier,
    groups: OrderedMap<String, ExerciseGroup>,
    exercises: OrderedMap<String, Exercise>,
    mainViewModel: MainViewModel,
    showExercise: (String) -> Unit
) {
    val exerciseList = exercises.toList()
    if (mainViewModel.editMode) {
        EditExercises(
            modifier,
            groups,
            mainViewModel::swapGroups,
            mainViewModel::editGroupButtonClicked,
            mainViewModel::editExerciseButtonClicked,
            mainViewModel::removeGroup,
            exerciseList,
            mainViewModel::swapExercises,
            mainViewModel::removeExercise
        )
    } else {
        LazyColumn(modifier = modifier) {
            items(groups) { id, group ->
                if (group == null) return@items
                GroupView(group, exercises, showExercise, toggleGroupCollapsed = {
                    mainViewModel.toggleGroupCollapsed(id)
                })
            }
            items(exercises) { key, exercise ->
                if (exercise?.showOnHomepage != true) return@items
                Box(modifier = Modifier.padding(horizontal = 15.dp)) {
                    ExerciseRow(exercise = exercise, showExercise = { showExercise(key) })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupView(
    group: ExerciseGroup,
    allExercises: Map<String, Exercise>,
    showExercise: (String) -> Unit,
    toggleGroupCollapsed: () -> Unit
) {
    Box(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = toggleGroupCollapsed
        )
    ) {
        if (group.collapsed) {
            Divider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(vertical = 18.dp)
                    .padding(horizontal = 10.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.onBackground)
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 13.dp)
            ) {
                if (group.notes.isNotEmpty()) {
                    Text(text = group.notes)
                }
                for (exerciseId in group.exerciseIds) {
                    val exercise = allExercises[exerciseId] ?: continue
                    ExerciseRow(exercise = exercise, showExercise = { showExercise(exerciseId) })
                }
            }
        }
        val titleHeightModifier = if (group.collapsed) {
            Modifier.padding(top = 10.dp)
        } else {
            Modifier
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .then(titleHeightModifier)
                .padding(start = 25.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 3.dp),
            text = group.title,
            style = LocalTextStyle.current.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
    }
}

@Composable
fun ExerciseRow(
    exercise: Exercise,
    showExercise: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 2.dp)
            .fillMaxWidth()
    ) {

        DefaultButton(
            onClick = showExercise
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (exercise.history.isNotEmpty()) {
                        Text(text = exercise.history.first().date?.toString() ?: "")
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .singleEdge(MaterialTheme.colorScheme.onPrimary, 2.dp, Side.Start)
                    ) {
                        if (exercise.history.isNotEmpty()) {
                            for (workout in exercise.history.first().sets) {
                                Text(
                                    text = stringResource(
                                        id = R.string.sets_by_reps,
                                        workout.weight.toString(),
                                        workout.reps
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}