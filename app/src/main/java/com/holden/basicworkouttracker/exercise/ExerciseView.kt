package com.holden.basicworkouttracker.exercise

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseView(
    exercisekey: String?,
    mainViewModel: MainViewModel,
    onDaySelected: (index: Int, showCreateNewWorkout: Boolean) -> Unit
) {
    val exercise = mainViewModel.exercisesAsState[exercisekey ?: return EmptyExerciseView()]
        ?: return EmptyExerciseView()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = exercise.title,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(15.dp)
            )
            if (exercise.history.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.onBackground)
                        .padding(10.dp)
                ){
                    Text(
                        text = "Last time",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Divider(thickness = 2.dp, modifier = Modifier.padding(5.dp))
                    SetListView(
                        sets = exercise.history.first().sets,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            var deleteIndex by remember {
                mutableStateOf<Int?>(null)
            }
            LazyColumn(
                modifier = Modifier.padding(start = 15.dp)
            ) {
                items(exercise.history.size) { dayIndex ->
                    val exerciseForDay = exercise.history[dayIndex]
                    val showDelete = deleteIndex == dayIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    deleteIndex = if (showDelete) null else dayIndex
                                }
                            )
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(.6f),
                            onClick = { onDaySelected(dayIndex, false) }
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = exerciseForDay.date.toString()
                            )
                            Text(text = "${exerciseForDay.sets.size} sets")
                        }
                        if (showDelete) {
                            IconButton(onClick = {
                                mainViewModel.removeDay(exercisekey, dayIndex)
                                deleteIndex = null
                            }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete day"
                                )
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = {
                val day = ExerciseForDay(currentDate(), listOf())
                mainViewModel.addDay(exercisekey, day)
                onDaySelected(0, true)
            }
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add workout day")
                Text(text = "New Day")
            }
        }
    }

}


@Composable
fun EmptyExerciseView() {

}
