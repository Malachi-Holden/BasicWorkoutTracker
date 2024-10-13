package com.holden.basicworkouttracker.exercise

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.LOCAL_PLATES
import com.holden.basicworkouttracker.MainViewModel
import com.holden.basicworkouttracker.PlatesToWeight
import com.holden.basicworkouttracker.R
import com.holden.basicworkouttracker.WeightToPlates
import com.holden.basicworkouttracker.loadPlates
import com.holden.basicworkouttracker.savePlates
import com.holden.basicworkouttracker.util.ModalView
import com.holden.basicworkouttracker.util.Side
import com.holden.basicworkouttracker.util.singleEdge


@Composable
fun ExerciseForDayView(
    exercisekey: String?,
    day: Int?,
    showNewWorkoutView: Boolean,
    mainViewModel: MainViewModel,
) {
    val exercise = mainViewModel.exercisesAsState[exercisekey]
    val title = exercise?.title
    if (day == null || title == null || exercisekey == null) return EmptyDayView()
    val exerciseForDay = exercise.history[day]
    Box {
        var editSetIndex by remember {
            mutableStateOf<Int?>(null)
        }
        var copySetIndex by remember {
            mutableStateOf<Int?>(null)
        }
        var weightForCalculator by remember {
            mutableStateOf<Double?>(null)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .singleEdge(MaterialTheme.colorScheme.onBackground, 2.dp, Side.Bottom)
                    .padding(5.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.displayLarge)
                Text(
                    text = exerciseForDay.date.toString(),
                    style = MaterialTheme.typography.displaySmall
                )
            }
            SetListView(
                sets = exerciseForDay.sets,
                modifier = Modifier.padding(15.dp),
                onDeleteSet = { mainViewModel.removeSet(exercisekey, day, it) },
                onUpdateSet = { editSetIndex = it },
                onCopySet = {
                    copySetIndex = it
                },
                onShowCalculator = { weightForCalculator = it }
            )
        }
        var showAddSet by remember {
            mutableStateOf(showNewWorkoutView)
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = { showAddSet = true }
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add set")
                Text(text = "New Set")
            }
        }
        AddSetPopup(
            title = "Add Set",
            confirmButtonTitle = "Add",
            showAddSet = showAddSet,
            onClose = { showAddSet = false },
            onAdd = {
                mainViewModel.addSet(exercisekey, day, it)
            }
        )
        val showEditSet = editSetIndex != null
        AddSetPopup(
            title = "Update Set",
            confirmButtonTitle = "Update",
            initialWorkout = editSetIndex?.let { exercise.history[day].sets[it] },
            showAddSet = showEditSet,
            onClose = { editSetIndex = null },
            onAdd = {
                mainViewModel.updateSet(exercisekey, day, editSetIndex, it)
                editSetIndex = null
            },
        )
        val showCopySet = copySetIndex != null
        AddSetPopup(
            title = "Copy Set",
            confirmButtonTitle = "Copy",
            initialWorkout = copySetIndex?.let { exercise.history[day].sets[it] },
            showAddSet = showCopySet,
            onClose = { copySetIndex = null },
            onAdd = {
                mainViewModel.addSet(exercisekey, day, it)
                copySetIndex = null
            },
        )
        ModalView(visible = weightForCalculator != null, onClose = { weightForCalculator = null }) {
            val context = LocalContext.current
            val persistedPlates = context.loadPlates(LOCAL_PLATES)
            WeightToPlates(
                weightForCalculator,
                persistedPlates?.second ?: listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5),
                persistedPlates?.first ?: 45.0,
                savePlates = { bar, weights ->
                    context.savePlates(LOCAL_PLATES, weights, bar)
                }
            )
        }
    }

}

@Composable
fun AddSetPopup(
    title: String,
    confirmButtonTitle: String,
    initialWorkout: Workout? = null,
    showAddSet: Boolean,
    onClose: () -> Unit,
    onAdd: (Workout) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var showCalculator by remember {
            mutableStateOf(false)
        }
        var setWeightFromCalculator: ((String) -> Unit)? = null
        ModalView(visible = showAddSet, onClose = onClose) {
            val (weight, setWeight) = remember {
                mutableStateOf(initialWorkout?.weight?.toString() ?: "")
            }
            setWeightFromCalculator = setWeight
            val (reps, setReps) = remember {
                mutableStateOf(initialWorkout?.reps?.toString() ?: "")
            }
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(text = title)
                val repsInt = reps.toIntOrNull()
                val weightDouble = weight.toDoubleOrNull()

                val workout = if (repsInt == null || weightDouble == null) null else Workout(
                    repsInt,
                    weightDouble
                )

                TextField(
                    value = reps,
                    onValueChange = setReps,
                    placeholder = { Text(text = "Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = weight,
                        onValueChange = setWeight,
                        placeholder = { Text(text = "Weight") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Button(onClick = { showCalculator = true }) {
                        Text(text = "Calculate by plates")
                    }
                }

                Row {
                    Button(onClick = onClose) {
                        Text(text = "Cancel")
                    }
                    Button(
                        enabled = workout != null,
                        onClick = {
                            workout?.let(onAdd)
                            onClose()
                        }
                    ) {
                        Text(text = confirmButtonTitle)
                    }
                }
            }
        }
        if (showCalculator) {
            Box(
                modifier = Modifier
                    .fillMaxSize(.8f)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PlatesToWeight(
                    onApply = {
                        setWeightFromCalculator?.let { setWeight -> setWeight(it.toString()) }
                        showCalculator = false
                    },
                    onCancel = {
                        showCalculator = false
                    }
                )
            }
        }

    }

}

@Composable
fun SetListView(
    sets: List<Workout>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    onDeleteSet: ((Int) -> Unit)? = null,
    onUpdateSet: ((Int) -> Unit)? = null,
    onCopySet: ((Int) -> Unit)? = null,
    onShowCalculator: ((Double) -> Unit)? = null
) {
    var actionsIndex by remember {
        mutableStateOf<Int?>(null)
    }
    LazyColumn(modifier = modifier) {
        items(sets.size) { setIndex ->
            val workout = sets[setIndex]
            val showActions = setIndex == actionsIndex
            if (onDeleteSet == null || onUpdateSet == null || onCopySet == null) {
                Text(
                    text = "${workout.weight} lbs x ${workout.reps}",
                    style = textStyle,
                    fontWeight = fontWeight
                )
            } else {
                SetActionRow(
                    onLongPress = { actionsIndex = if (showActions) null else setIndex },
                    onDeleteSet = {
                        onDeleteSet(setIndex)
                        actionsIndex = null
                    },
                    onUpdateSet = { onUpdateSet(setIndex) },
                    onCopySet = { onCopySet(setIndex) },
                    workout = workout,
                    showActions = showActions,
                    onCalculatePlates = { onShowCalculator?.let { it(workout.weight) } }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SetActionRow(
    onLongPress: () -> Unit,
    onDeleteSet: () -> Unit,
    onUpdateSet: () -> Unit,
    onCopySet: () -> Unit,
    onCalculatePlates: () -> Unit,
    workout: Workout,
    showActions: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongPress),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${workout.weight} lbs x ${workout.reps}")
        Box() {
            Row() {
                IconButton(onClick = onDeleteSet) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete set",
                        modifier = Modifier.padding(10.dp),
                    )
                }
                IconButton(onClick = onUpdateSet) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit set",
                        modifier = Modifier.padding(10.dp),
                    )
                }
                IconButton(onClick = onCopySet) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.copy), contentDescription = "Copy")
                }
                Button(
                    onClick = onCalculatePlates
                ) {
                    Text(text = "Calculate plates")
                }
            }
            if (!showActions) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .matchParentSize()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = onLongPress
                        )
                )
            }

        }
    }
}


@Composable
fun EmptyDayView() {

}