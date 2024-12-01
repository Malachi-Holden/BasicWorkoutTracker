package com.holden.basicworkouttracker.exercise.day

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.LOCAL_PLATES
import com.holden.basicworkouttracker.PlatesToWeight
import com.holden.basicworkouttracker.R
import com.holden.basicworkouttracker.WeightToPlates
import com.holden.basicworkouttracker.exercise.Workout
import com.holden.basicworkouttracker.savePlates
import com.holden.basicworkouttracker.ui.theme.DefaultButton
import com.holden.basicworkouttracker.util.ModalView
import com.holden.basicworkouttracker.util.Side
import com.holden.basicworkouttracker.util.singleEdge
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime


@Composable
fun ExerciseForDayView(
    dayViewModel: DayViewModel
) {
    val exercise = dayViewModel.exerciseState
    val title = exercise?.title
    if (dayViewModel.dayIndex == null || title == null) return EmptyDayView()
    val exerciseForDay = exercise.history[dayViewModel.dayIndex]
    Box {
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
                    modifier = Modifier.clickable {
                        dayViewModel.showCalendar()
                    },
                    text = exerciseForDay.date.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }
            SetListView(
                sets = exerciseForDay.sets,
                modifier = Modifier.padding(15.dp),
                onDeleteSet = dayViewModel::removeSet,
                onUpdateSet = dayViewModel::showUpdateSet,
                onCopySet = dayViewModel::showCopySet,
                onShowCalculator = dayViewModel::onShowCalculator
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = { dayViewModel.showAddSet.value = true }
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_set))
                Text(text = stringResource(R.string.new_set))
            }
        }
        AddSetPopup(
            title = stringResource(R.string.add_set),
            confirmButtonTitle = "Add",
            showAddSet = dayViewModel.showAddSet.collectAsState().value,
            onClose = { dayViewModel.showAddSet.value = false },
            onAdd = dayViewModel::addSet
        )
        AddSetPopup(
            title = stringResource(id = R.string.update_set),
            confirmButtonTitle = stringResource(id = R.string.update),
            initialWorkout = dayViewModel.workoutToUpdate,
            showAddSet = dayViewModel.showEditSet,
            onClose = dayViewModel::closeUpdateSetPopup,
            onAdd = dayViewModel::updateSet,
        )
        AddSetPopup(
            title = stringResource(id = R.string.copy_set),
            confirmButtonTitle = stringResource(id = R.string.copy),
            initialWorkout = dayViewModel.workoutToCopy,
            showAddSet = dayViewModel.showCopySet,
            onClose = dayViewModel::closeCopySetPopup,
            onAdd = {
                dayViewModel.addSet(it)
                dayViewModel.closeCopySetPopup()
            },
        )
        ModalView(visible = dayViewModel.showCalculator, onClose = dayViewModel::hideCalculator) {
            val context = LocalContext.current
            val persistedPlates = dayViewModel.loadPersistedPlates()
            WeightToPlates(
                dayViewModel.weightForCalculator,
                persistedPlates.second,
                persistedPlates.first,
                savePlates = { bar, weights ->
                    context.savePlates(LOCAL_PLATES, weights, bar)
                }
            )
        }
        DayPickerView(
            exerciseForDay.date?.atStartOfDayIn(TimeZone.UTC),
            dayViewModel.showCalendar,
            onDateUpdated = dayViewModel::onDateUpdated,
            onClose =  dayViewModel::hideCalendar
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPickerView(
    currentDate: Instant?,
    showPicker: Boolean,
    onDateUpdated: (Instant) -> Unit,
    onClose: () -> Unit
) {
    ModalView(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        visible = showPicker,
        modalProportion = .9f,
        onClose = onClose
    ) {
        Column {
            val state = rememberDatePickerState(
                currentDate?.toEpochMilliseconds()
            )
            DatePicker(
                state = state
            )
            Row {
                DefaultButton(onClick = onClose) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                DefaultButton(onClick = {
                    val millis = (state.selectedDateMillis ?: return@DefaultButton)
                    onDateUpdated(
                        Instant
                            .fromEpochMilliseconds(millis)
                    )
                }) {
                    Text(text = stringResource(id = R.string.update))
                }
            }
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
                    placeholder = { Text(text = stringResource(id = R.string.reps)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = weight,
                        onValueChange = setWeight,
                        placeholder = { Text(text = stringResource(id = R.string.weight)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Button(onClick = { showCalculator = true }) {
                        Text(text = stringResource(id = R.string.calculate_by_plates))
                    }
                }

                Row {
                    Button(onClick = onClose) {
                        Text(text = stringResource(id = R.string.cancel))
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
                    text = stringResource(id = R.string.sets_by_reps, workout.weight.toString(), workout.reps),
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
        Text(text = stringResource(id = R.string.sets_by_reps, workout.weight.toString(), workout.reps))
        Box() {
            Row() {
                IconButton(onClick = onDeleteSet) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete_set),
                        modifier = Modifier.padding(10.dp),
                    )
                }
                IconButton(onClick = onUpdateSet) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit_set),
                        modifier = Modifier.padding(10.dp),
                    )
                }
                IconButton(onClick = onCopySet) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.copy), contentDescription = "Copy")
                }
                DefaultButton(
                    onClick = onCalculatePlates
                ) {
                    Text(text = stringResource(id = R.string.calculate_plates))
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