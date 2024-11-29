package com.holden.basicworkouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.home.MainViewModel
import com.holden.basicworkouttracker.ui.theme.BasicWorkoutTrackerTheme
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.buildFactory
import com.holden.basicworkouttracker.util.orderedMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicWorkoutTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var exercises = loadExercises(LOCAL_EXERCISES) ?: orderedMapOf()
                    val ids = if (exercises.isNotEmpty()) {
                        exercises.toList().map { it.first }
                    } else {
                        listOf()
                    }
                    val group = ExerciseGroup("First group", ids.subList(0, ids.size/2))
                    exercises = exercises.replace(exercises[group.exerciseIds.first()]!!.copy(
                        showOnHomepage = false
                    ), group.exerciseIds.first())
                    val mainViewModel: MainViewModel = viewModel(
                        factory = buildFactory { MainViewModel(orderedMapOf(UUID.randomUUID().toString() to group), exercises, ::saveExercises) }
                    )
                    MainNavHost(navController = rememberNavController(), mainViewModel = mainViewModel)
                }
            }
        }
    }

    fun saveExercises(exercises: OrderedMap<String, Exercise>) {
        lifecycleScope.launch(Dispatchers.IO) {
            saveExercises(LOCAL_EXERCISES, exercises)
        }
    }
}
