package com.holden.basicworkouttracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.home.MainViewModel
import com.holden.basicworkouttracker.persistence.LOCAL_EXERCISES
import com.holden.basicworkouttracker.persistence.LOCAL_GROUPS
import com.holden.basicworkouttracker.persistence.loadExercises
import com.holden.basicworkouttracker.persistence.loadGroups
import com.holden.basicworkouttracker.persistence.saveExercises
import com.holden.basicworkouttracker.persistence.saveGroups
import com.holden.basicworkouttracker.ui.theme.BasicWorkoutTrackerTheme
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.buildFactory
import com.holden.basicworkouttracker.util.orderedMapOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicWorkoutTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavHost()
                }
            }
        }
    }
}
