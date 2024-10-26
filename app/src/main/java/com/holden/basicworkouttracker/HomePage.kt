package com.holden.basicworkouttracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.ui.theme.DefaultButton
import com.holden.basicworkouttracker.util.ModalView
import com.holden.basicworkouttracker.util.Side
import com.holden.basicworkouttracker.util.items
import com.holden.basicworkouttracker.util.singleEdge

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    mainViewModel: MainViewModel,
    showExercise: (String) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)) {
        val exercises = mainViewModel.exercisesAsState
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(exercises) { (key, exercise) ->
                exercise ?: return@items
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.rowClicked(key)
                        }
                ) {
                    DefaultButton(
                        onClick = { showExercise(key) },
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
                    if (mainViewModel.deleteKey == key) {
                        IconButton(onClick = { mainViewModel.removeExercise(key) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Exercise"
                            )
                        }
                    }
                }

            }
        }

        FloatingActionButton(onClick = mainViewModel::addButtonClicked, modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(20.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add exercise")
        }
        AddExercisePopup(
            showPopup = mainViewModel.showAddExercise,
            onExerciseCreated = {
                mainViewModel.addExercise(it)
            },
            onPopupClosed = mainViewModel::onPopupClosed
        )
    }
}

@Composable
fun AddExercisePopup(
    showPopup: Boolean,
    onExerciseCreated: (Exercise) -> Unit,
    onPopupClosed: () -> Unit
) {
    ModalView(visible = showPopup, onClose = onPopupClosed) {
        Column {
            Text(text = "New Exercise")
            val (title, setTitle) = remember {
                mutableStateOf("")
            }
            TextField(value = title, onValueChange = setTitle, placeholder = { Text(text = "Name") })
            Row {
                DefaultButton(onClick = onPopupClosed) {
                    Text(text = "Cancel")
                }
                DefaultButton(onClick = {
                    onExerciseCreated(Exercise(title, listOf()))
                    onPopupClosed()
                }) {
                    Text(text = "Create")
                }
            }
        }
    }
}