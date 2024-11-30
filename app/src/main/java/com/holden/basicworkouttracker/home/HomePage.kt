package com.holden.basicworkouttracker.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
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
            var editing by remember {
                mutableStateOf(false)
            }
            EditableExerciseList(
                editing = editing,
                groups = groups,
                exercises = exercises,
                onSwapExercises = mainViewModel::swapExercises,
                onSwapGroups = mainViewModel::swapGroups,
                removeExercise = mainViewModel::removeExercise,
                removeGroup = mainViewModel::removeGroup,
                showExercise = showExercise,
                editGroup = mainViewModel::editGroupButtonClicked,
                editExercise = mainViewModel::editExerciseButtonClicked
            )
            Row {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    ),
                    onClick = { editing = !editing }
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = if (editing) "end editing" else "edit"
                    )
                }
                if (editing) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        ),
                        onClick = mainViewModel::addExerciseButtonClicked
                    ) {
                        Text(
                            color = MaterialTheme.colorScheme.onBackground,
                            text = "Add Exercise"
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
                            text = "Add Group"
                        )
                    }
                }

            }
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
            "Edit Exercise Group",
            doneButtonText = "Save",
            onFinishedEditing = mainViewModel::onEditGroupComplete,
            onPopupClosed = mainViewModel::onEditGroupPopupClosed
        )
        AddExercisePopup(
            showPopup = mainViewModel.showAddExercise,
            onExerciseCreated = mainViewModel::addExercise,
            onPopupClosed = mainViewModel::onNewExercisePopupClosed
        )
        EditExercisePopup(
            headerText = "Edit Exercise",
            doneText = "Save",
            initialExercise = mainViewModel.editingExercise,
            showPopup = mainViewModel.editingExercise != null,
            onExerciseUpdated = mainViewModel::onEditExerciseComplete,
            onPopupClosed = mainViewModel::onEditExercisePopupClosed
        )
    }
}

@Composable
fun EditableExerciseList(
    editing: Boolean,
    groups: OrderedMap<String, ExerciseGroup>,
    exercises: OrderedMap<String, Exercise>,
    onSwapExercises: (Int, Int) -> Unit,
    onSwapGroups: (Int, Int) -> Unit,
    removeExercise: (String) -> Unit,
    removeGroup: (String) -> Unit,
    showExercise: (String) -> Unit,
    editGroup: (String) -> Unit,
    editExercise: (String) -> Unit
) {
    val exerciseList = exercises.toList()
    if (editing) {
        EditExercises(
            groups,
            onSwapGroups,
            editGroup,
            editExercise,
            removeGroup,
            exerciseList,
            onSwapExercises,
            removeExercise
        )
    } else {
        LazyColumn {
            items(groups) { _, group ->
                if (group == null) return@items
                GroupView(group, exercises, showExercise)
            }
            items(exercises) { key, exercise ->
                if (exercise?.showOnHomepage != true) return@items
                Box(modifier = Modifier.padding(start = 15.dp)) {
                    ExerciseRow(exercise = exercise, showExercise = { showExercise(key) })
                }
            }
        }
    }
}

@Composable
fun GroupView(
    group: ExerciseGroup,
    allExercises: Map<String, Exercise>,
    showExercise: (String) -> Unit
) {
    Box{
        Column(
            modifier = Modifier
                .padding(start = 5.dp)
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.onBackground)
                .padding(start = 10.dp)
                .padding(vertical = 13.dp)
        ) {
            for (exerciseId in group.exerciseIds) {
                val exercise = allExercises[exerciseId] ?: continue
                ExerciseRow(exercise = exercise, showExercise = { showExercise(exerciseId) })
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
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
            .fillMaxWidth()
    ) {
        DefaultButton(
            onClick = showExercise,
            modifier = Modifier.fillMaxWidth(.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Column(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .singleEdge(MaterialTheme.colorScheme.onPrimary, 2.dp, Side.Start)
                ) {
                    if (exercise.history.isNotEmpty()) {
                        for (workout in exercise.history.first().sets) {
                            Text(
                                text = "${workout.weight} lbs x ${workout.reps}",
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}