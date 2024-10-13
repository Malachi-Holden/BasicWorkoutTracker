package com.holden.basicworkouttracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.holden.basicworkouttracker.exercise.ExerciseForDayView
import com.holden.basicworkouttracker.exercise.ExerciseView

enum class Nav {
    Home, Exercise, Day
}

@Composable
fun MainNavHost(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    var currentExercise by remember {
        mutableStateOf<String?>(null)
    }
    var currentDayIndex by remember {
        mutableStateOf<Int?>(null)
    }
    var showCreateWorkoutView by remember {
        mutableStateOf(false)
    }
    NavHost(navController = navController, startDestination = Nav.Home.name) {
        composable(Nav.Home.name) {
            HomePage(mainViewModel = mainViewModel, showExercise = {
                currentExercise = it
                navController.navigate(Nav.Exercise.name)
            })
        }
        composable(Nav.Exercise.name) {
            ExerciseView(
                mainViewModel = mainViewModel,
                exercisekey = currentExercise,
                onDaySelected = { index, showCreateWorkout ->
                    showCreateWorkoutView = showCreateWorkout
                    currentDayIndex = index
                    navController.navigate(Nav.Day.name)
                }
            )
        }
        composable(Nav.Day.name) {
            ExerciseForDayView(
                exercisekey = currentExercise,
                day = currentDayIndex,
                mainViewModel = mainViewModel,
                showNewWorkoutView = showCreateWorkoutView
            )
        }
    }
}