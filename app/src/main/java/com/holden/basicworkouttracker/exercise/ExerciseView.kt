package com.holden.basicworkouttracker.exercise

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.R
import com.holden.basicworkouttracker.exercise.day.SetListView
import com.holden.basicworkouttracker.home.ConfirmationPopup
import com.holden.basicworkouttracker.ui.theme.DefaultButton

@Composable
fun ExerciseView(
    exerciseViewModel: ExerciseViewModel,
    navigateToDay: () -> Unit,
    onExerciseRemoved: () -> Unit
) {
    val exercise = exerciseViewModel.exerciseState
        ?: return EmptyExerciseView()
    Box(modifier = Modifier.fillMaxSize()) {
        var showDeleteExerciseConfirmation by remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                modifier = Modifier.padding(15.dp),
                value = exercise.title,
                onValueChange = exerciseViewModel::updateTitle,
                textStyle = MaterialTheme.typography.displayLarge,
                placeholder = { Text(text = stringResource(R.string.name)) }
            )

            TextField(
                modifier = Modifier.padding(5.dp).fillMaxWidth(),
                value = exercise.notes,
                onValueChange = exerciseViewModel::updateNotes,
                placeholder = { Text(text = stringResource(id = R.string.notes)) }
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
                        text = stringResource(id = R.string.last_time),
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
                text = stringResource(id = R.string.history),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            val deleteDayIndex = exerciseViewModel.deleteDayIndex
            LazyColumn(
                modifier = Modifier.padding(start = 15.dp)
            ) {
                items(exercise.history.size) { dayIndex ->
                    val exerciseForDay = exercise.history[dayIndex]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { exerciseViewModel.dayClicked(dayIndex) }
                    ) {
                        DefaultButton(
                            modifier = Modifier.fillMaxWidth(.6f),
                            onClick = {
                                exerciseViewModel.onDaySelected(dayIndex, false)
                                navigateToDay()
                            }
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = exerciseForDay.date.toString()
                            )
                            Text(text = pluralStringResource(
                                id = R.plurals.sets,
                                exerciseForDay.sets.size,
                                exerciseForDay.sets.size
                            ))
                        }
                        if (deleteDayIndex == dayIndex) {
                            IconButton(onClick = {
                                exerciseViewModel.removeDay(dayIndex)
                            }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = stringResource(id = R.string.delete_day)
                                )
                            }
                        }
                    }
                }
                item {
                    DefaultButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            showDeleteExerciseConfirmation = true
                        }
                    ) {
                        Text(stringResource(R.string.delete_exercise))
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = {
                val day = ExerciseForDay("", currentDate(), listOf())
                exerciseViewModel.addDay(day)
                exerciseViewModel.onDaySelected(0, true)
                navigateToDay()
            }
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = stringResource(id = R.string.add_workout_day)
                )
                Text(text = stringResource(id = R.string.new_day))
            }
        }

        ConfirmationPopup(
            showPopup = showDeleteExerciseConfirmation,
            onConfirmed = {
                showDeleteExerciseConfirmation = false
                exerciseViewModel.deleteExercise()
                onExerciseRemoved()
            },
            bodyText = stringResource(R.string.delete_exercise_confirmation, exercise.title),
            confirmText = stringResource(R.string.delete_exercise),
            onCancelled = {
                showDeleteExerciseConfirmation = false
            }
        )
    }

}


@Composable
fun EmptyExerciseView() {
}
