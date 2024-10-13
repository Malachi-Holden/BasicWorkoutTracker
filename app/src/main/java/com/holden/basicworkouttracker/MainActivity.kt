package com.holden.basicworkouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.ui.theme.BasicWorkoutTrackerTheme
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.buildFactory
import com.holden.basicworkouttracker.util.orderedMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicWorkoutTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = viewModel(
                        factory = buildFactory { MainViewModel(loadExercises(LOCAL_EXERCISES) ?: orderedMapOf(), ::saveExercises) }
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
