package com.holden.basicworkouttracker.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.util.DragDropColumn
import com.holden.basicworkouttracker.util.OrderedMap

@Composable
fun EditExercises(
    modifier: Modifier,
    groups: OrderedMap<String, ExerciseGroup>,
    onSwapGroups: (Int, Int) -> Unit,
    editGroup: (String) -> Unit,
    editExercise: (String) -> Unit,
    removeGroup: (String) -> Unit,
    exerciseList: List<Pair<String, Exercise>>,
    onSwapExercises: (Int, Int) -> Unit,
    removeExercise: (String) -> Unit
) {
    val groupList = groups.toList()
    Column(modifier = modifier) {
        Text(text = "Groups")
        DragDropColumn(items = groupList, onSwap = onSwapGroups) { _, (id, group) ->
            EditGroupRow(
                group = group,
                onEditClicked = { editGroup(id) },
                removeGroup = { removeGroup(id) }
            )
        }
        Text(text = "Exercises")
        DragDropColumn(items = exerciseList, onSwap = onSwapExercises) { _, (id, exercise) ->
            EditExerciseRow(
                exercise = exercise,
                onEditClicked = { editExercise(id) },
                removeExercise = { removeExercise(id) }
            )
        }
    }
}


@Composable
fun EditGroupRow(
    group: ExerciseGroup,
    onEditClicked: () -> Unit,
    removeGroup: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = group.title,
            style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic)
        )
        IconButton(onClick = removeGroup) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Group"
            )
        }
        IconButton(onClick = onEditClicked) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Group"
            )
        }
    }
}


@Composable
fun EditExerciseRow(
    exercise: Exercise,
    onEditClicked: () -> Unit,
    removeExercise: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 15.dp)
            .fillMaxWidth()
    ){
        Text(
            text = exercise.title,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = removeExercise) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Exercise"
            )
        }
        IconButton(onClick = onEditClicked) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Exercise"
            )
        }
    }
}