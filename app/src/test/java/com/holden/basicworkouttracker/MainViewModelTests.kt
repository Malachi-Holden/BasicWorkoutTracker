package com.holden.basicworkouttracker

import com.holden.basicworkouttracker.di.Persistence
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.home.MainViewModel
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.orderedMapOf
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.fail

class MainViewModelTests {
    @Test
    fun `editExercise sets and saves the exercise`() {
        var savedExercises: OrderedMap<String, Exercise>? = null
        val initialExercise = Exercise("hello", history = listOf())
        val viewmodel = MainViewModel(
            Persistence(
                orderedMapOf("1" to initialExercise),
                orderedMapOf(),
                {_,exercises-> savedExercises = exercises },
                {_,_->}
            )
        )
        val newExercise = Exercise("goodbye", history = listOf())
        viewmodel.editExercise("1", newExercise)
        assertEquals(newExercise, viewmodel._exercisesFlow.value["1"])
        assertEquals(orderedMapOf("1" to newExercise), savedExercises)
    }

    @Test
    fun `editGroup sets and saves the group`() {
        var savedGroups: OrderedMap<String, ExerciseGroup>? = null
        val initialGroup = ExerciseGroup("hello", exerciseIds = listOf())
        val viewmodel = MainViewModel(
            Persistence(
                orderedMapOf(),
                orderedMapOf("1" to initialGroup),
                {_,_->},
                {_,groups-> savedGroups = groups}
            )
        )
        val newGroup = ExerciseGroup("goodbye", exerciseIds = listOf())
        viewmodel.editGroup("1", newGroup)
        assertEquals(newGroup, viewmodel._groupsFlow.value["1"])
        assertEquals(orderedMapOf("1" to newGroup), savedGroups)
    }

    @Test
    fun `onEditGroupComplete does nothing if there is no group being edited`() {
        val initialGroup = ExerciseGroup("hello", exerciseIds = listOf())
        val viewmodel = MainViewModel(
            Persistence(
                orderedMapOf(),
                orderedMapOf("1" to initialGroup),
                {_,_->},
                {_,_-> fail("save groups should not be called if id is null") }
            )
        )
        viewmodel.onEditGroupComplete(ExerciseGroup("goodbye", exerciseIds = listOf()))
        assertEquals(initialGroup, viewmodel._groupsFlow.value["1"])
    }

    @Test
    fun `onEditGroupComplete sets and saves the group if editing button has been clicked`() {
        var savedGroups: OrderedMap<String, ExerciseGroup>? = null
        val initialGroup = ExerciseGroup("hello", exerciseIds = listOf())
        val viewmodel = MainViewModel(
            Persistence(
                orderedMapOf(),
                orderedMapOf("1" to initialGroup),
                {_,_->},
                {_,groups-> savedGroups = groups}
            )
        )
        val newGroup = ExerciseGroup("goodbye", exerciseIds = listOf())
        viewmodel.editGroupButtonClicked("1")
        viewmodel.onEditGroupComplete(newGroup)
        assertEquals(newGroup, viewmodel._groupsFlow.value["1"])
        assertEquals(orderedMapOf("1" to newGroup), savedGroups)
    }

    @Test
    fun `onEditExerciseComplete sets and saves the exercise if editing button has been clicked`() {
        var savedExercises: OrderedMap<String, Exercise>? = null
        val initialGroup = Exercise("hello", history = listOf())
        val viewmodel = MainViewModel(
            Persistence(
                orderedMapOf("1" to initialGroup),
                orderedMapOf(),
                {_,exercises-> savedExercises = exercises},
                {_,_->},
            )
        )
        val newExercise = Exercise("goodbye", history = listOf())
        viewmodel.editExerciseButtonClicked("1")
        viewmodel.onEditExerciseComplete(newExercise)
        assertEquals(newExercise, viewmodel._exercisesFlow.value["1"])
        assertEquals(orderedMapOf("1" to newExercise), savedExercises)
    }
}