package com.holden.basicworkouttracker.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.ui.theme.DefaultButton
import com.holden.basicworkouttracker.util.DragDropColumn
import com.holden.basicworkouttracker.util.ModalView
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.filter
import com.holden.basicworkouttracker.util.items
import com.holden.basicworkouttracker.util.swapped

@Composable
fun AddGroupPopup(
    showPopup: Boolean,
    exercises: OrderedMap<String, Exercise>,
    onGroupCreated: (ExerciseGroup) -> Unit,
    onPopupClosed: () -> Unit
) {
    EditGroupPopup(
        null,
        showPopup,
        exercises,
        "New Exercise Group",
        "Create",
        onGroupCreated,
        onPopupClosed
    )
}

@Composable
fun EditGroupPopup(
    initialGroup: ExerciseGroup?,
    showPopup: Boolean,
    exercises: OrderedMap<String, Exercise>,
    headerText: String,
    doneButtonText: String,
    onFinishedEditing: (ExerciseGroup) -> Unit,
    onPopupClosed: () -> Unit
) {
    ModalView(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp),
        visible = showPopup,
        onClose = onPopupClosed
    ) {
        Column {
            var group by remember {
                mutableStateOf(initialGroup ?: ExerciseGroup("", listOf()))
            }
            Text(text = headerText)
            TextField(
                value = group.title,
                onValueChange = { group = group.copy(title = it) },
                placeholder = { Text(text = "Name") }
            )
            DragDropColumn(
                items = group.exerciseIds,
                onSwap = { first, second ->
                    group = group.copy(
                        exerciseIds = group.exerciseIds.swapped(first, second)
                    )
                }
            ) { i, id ->
                val exercise = exercises[id] ?: return@DragDropColumn
                Row {
                    Text(text = exercise.title)
                    IconButton(onClick = { group = group.copy(exerciseIds = group.exerciseIds.filter { it != id }) }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Remove exercise from group")
                    }
                }
            }
            val groupIdSet = group.exerciseIds.toSet()
            LazyColumn {
                items(exercises.filter { it.first !in groupIdSet }) { id, exercise ->
                    if (exercise == null) return@items
                    Row {
                        Text(text = exercise.title)
                        IconButton(onClick = { group = group.copy(exerciseIds = group.exerciseIds + id) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add exercise to group")
                        }
                    }
                }
                item {
                    Row {
                        DefaultButton(onClick = onPopupClosed) {
                            Text(text = "Cancel")
                        }
                        DefaultButton(onClick = {
                            onFinishedEditing(group)
                            onPopupClosed()
                        }) {
                            Text(text = doneButtonText)
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun AddExercisePopup(
    showPopup: Boolean,
    onExerciseCreated: (Exercise) -> Unit,
    onPopupClosed: () -> Unit
) {
    EditExercisePopup(
        headerText = "New Exercise",
        doneText = "Create",
        initialExercise = null,
        showPopup = showPopup,
        onExerciseUpdated = onExerciseCreated,
        onPopupClosed = onPopupClosed
    )
}

@Composable
fun EditExercisePopup(
    headerText: String,
    doneText: String,
    initialExercise: Exercise?,
    showPopup: Boolean,
    onExerciseUpdated: (Exercise) -> Unit,
    onPopupClosed: () -> Unit
) {
    ModalView(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp),
        visible = showPopup,
        onClose = onPopupClosed
    ) {
        Column {
            Text(text = headerText)
            var exercise by remember {
                mutableStateOf(initialExercise ?: Exercise("", listOf()))
            }
            TextField(
                value = exercise.title,
                onValueChange = { exercise = exercise.copy(title = it) },
                placeholder = { Text(text = "Name") }
            )
            Row {
                DefaultButton(onClick = onPopupClosed) {
                    Text(text = "Cancel")
                }
                DefaultButton(onClick = {
                    onExerciseUpdated(exercise)
                    onPopupClosed()
                }) {
                    Text(text = doneText)
                }
            }
        }
    }
}