package com.holden.basicworkouttracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.holden.basicworkouttracker.exercise.day.ExerciseForDayView
import com.holden.basicworkouttracker.exercise.ExerciseView
import com.holden.basicworkouttracker.home.HomePage
import com.holden.basicworkouttracker.home.MainViewModel

enum class Nav {
    Home, Exercise, Day
}

@Composable
fun MainNavHost(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val exerciseViewModel = mainViewModel.exerciseViewModel

    var dayIndex by remember {
        mutableStateOf<Int?>(null)
    }
    NavHost(navController = navController, startDestination = Nav.Home.name) {
        composable(Nav.Home.name) {
            HomePage(mainViewModel = mainViewModel, showExercise = {
                exerciseViewModel.exerciseKey.value = it
                navController.navigate(Nav.Exercise.name)
            })
        }
        composable(Nav.Exercise.name) {
            ExerciseView(
                exerciseViewModel = exerciseViewModel,
                onDaySelected = { index, showCreateWorkout ->
                    exerciseViewModel.showNewWorkoutOnNavigate = showCreateWorkout
                    dayIndex = index
                    navController.navigate(Nav.Day.name)
                }
            )
        }
        composable(Nav.Day.name) {
            ExerciseForDayView(exerciseViewModel.dayViewModel(dayIndex))
        }
    }
}