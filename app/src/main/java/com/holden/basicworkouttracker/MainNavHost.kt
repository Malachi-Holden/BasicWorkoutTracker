package com.holden.basicworkouttracker

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.holden.basicworkouttracker.exercise.day.ExerciseForDayView
import com.holden.basicworkouttracker.exercise.ExerciseView
import com.holden.basicworkouttracker.home.HomePage
import com.holden.basicworkouttracker.home.MainViewModel

enum class Nav {
    Home, Exercise, Day
}

@Composable
fun MainNavHost(
    mainViewModel: MainViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val exerciseViewModel = mainViewModel.exerciseViewModel

    NavHost(navController = navController, startDestination = Nav.Home.name) {
        composable(Nav.Home.name) {
            HomePage(
                mainViewModel = mainViewModel,
                showExercise = {
                    exerciseViewModel.exerciseKey.value = it
                    navController.navigate(Nav.Exercise.name)
                }
            )
        }
        composable(Nav.Exercise.name) {
            ExerciseView(
                exerciseViewModel = exerciseViewModel,
                navigateToDay = {
                    navController.navigate(Nav.Day.name)
                },
                onExerciseRemoved = {
                    navController.popBackStack()
                }
            )
        }
        composable(Nav.Day.name) {
            ExerciseForDayView(exerciseViewModel.dayViewModel())
        }
    }
}