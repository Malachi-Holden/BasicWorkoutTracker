package com.holden.basicworkouttracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holden.basicworkouttracker.util.mapNotNullorNull
import com.holden.basicworkouttracker.util.toDoubleWithWhiteSpaceOrNull

@Composable
fun PlatesToWeight(
    onApply: ((Double) -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    val (barbellInput, setBarbellInput) = remember {
        mutableStateOf("45")
    }
    val weightInputs = remember {
        mutableStateListOf<String>()
    }
    Column {
        Text(text = "Calculate weight", style = MaterialTheme.typography.displayMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "barbell:")
            TextField(
                value = barbellInput,
                onValueChange = setBarbellInput,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        Text(text = "Weight on both sides:")
        EditableTextColumn(
            inputList = weightInputs,
            keyboardType = KeyboardType.Decimal,
            placeHolderText = "Plate weight"
        )
        val weights = weightInputs.mapNotNullorNull { it.toDoubleWithWhiteSpaceOrNull() }
        val barbell = barbellInput.toDoubleWithWhiteSpaceOrNull()
        val result = if (barbell == null || weights == null) {
            null
        } else {
            platesToWeight(barbell, weights)
        }
        val message = if (result == null) {
            "Incorrect numerical entry"
        } else {
            "$result lbs"
        }
        Row {
            Text(text = message)
            if (onCancel != null) {
                Button(onClick = onCancel) {
                    Text(text = "Cancel")
                }
            }
            if (onApply != null) {
                Button(
                    onClick = {
                        if (result != null) {
                            onApply(result)
                        }
                    },
                    enabled = result != null) {
                    Text(text = "Apply")
                }
            }

        }
    }
}

fun platesToWeight(
    bar: Double,
    platesPerSide: List<Double>
): Double = bar + platesPerSide.sum() * 2

@Composable
fun WeightToPlates(
    goalWeight: Double? = null,
    initialPlates: List<Double> = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5),
    initialBar: Double = 45.0,
    savePlates: ((Double, List<Double>) -> Unit)? = null
) {
    val (goalWeightText, setGoalWeightText) = remember {
        mutableStateOf(goalWeight?.toString() ?: "")
    }
    val goalWeight = goalWeightText.toDoubleWithWhiteSpaceOrNull()
    val (barWeightText, setBarWeightText) = remember {
        mutableStateOf(initialBar.toString())
    }
    val availablePlatesText = remember {
        mutableStateListOf<String>()
            .apply { addAll(initialPlates.map { it.toString() }) }
    }
    val barWeight = barWeightText.toDoubleWithWhiteSpaceOrNull()
    val availablePlates = availablePlatesText.mapNotNullorNull { it.toDoubleWithWhiteSpaceOrNull() }

    val result = if (goalWeight == null || availablePlates == null || barWeight == null) {
        null
    } else {
        naiveWeightToPlates(goalWeight, barWeight, availablePlates.sortedDescending())
    }
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Text(text = "Calculate plates")
        TextField(
            value = goalWeightText,
            onValueChange = setGoalWeightText,
            placeholder = { Text(text = "Goal Weight") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text(text = "Plates per side:")
        val resultText = result
            ?.filter { (number, _) -> number > 0 }
            ?.joinToString(", "){ (number, plate) -> "$number x $plate lbs" }
            ?: "invalid numerical input"
        Text(text = resultText)
        val errorText = if (result == null || goalWeight == null || barWeight == null) {
            ""
        } else {
            val error = goalWeight - 2 * result.sumOf { (number, plate) -> number * plate } - barWeight
            "Error amount: $error"
        }
        Text(text = errorText)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Bar weight")
        TextField(
            value = barWeightText,
            onValueChange = setBarWeightText,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            placeholder = { Text(text = "Bar weight") }
        )

        Text(text = "Available Plates")
        EditableTextColumn(
            inputList = availablePlatesText,
            placeHolderText = "Plate weight",
            keyboardType = KeyboardType.Decimal
        )
        val saveButtonText = if (availablePlates == null) {
            "Invalid plate weight"
        } else {
            "Save plate configuration"
        }
        Button(
            onClick = {
                if (barWeight != null && availablePlates != null && savePlates != null) {
                    savePlates(barWeight, availablePlates)
                }
            },
            enabled = availablePlates == null
        ) {
            Text(text = saveButtonText)
        }
    }
}

@Preview
@Composable
fun WeightToPlatesPreview() {
    WeightToPlates()
}

@Composable
fun EditableTextColumn(
    inputList: SnapshotStateList<String>,
    placeHolderText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        LazyColumn {
            items(inputList.size) { i ->
                Row {
                    TextField(
                        value = inputList[i],
                        onValueChange = { inputList[i] = it },
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        placeholder = { Text(text = placeHolderText) },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { inputList.removeAt(i) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove item")
                    }
                }

            }
        }
        IconButton(onClick = { inputList.add("") }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add item")
        }
    }
}

/**
 * This algorithm will not be sucessful in a lot of edge cases but it works for most gym needs
 */
fun naiveWeightToPlates(
    weight: Double,
    bar: Double,
    allowedPlates: List<Double> // assumes they are sorted largest to smallest
): List<Pair<Int, Double>> = buildList {
    val weightPerSide = (weight - bar)/2
    var remainingWeight = weightPerSide
    for (plate in allowedPlates) {
        println(remainingWeight)
        if (remainingWeight < allowedPlates.last()) break
        val number = (remainingWeight/plate).toInt()
        add(number to plate)
        remainingWeight -= number * plate
    }
}