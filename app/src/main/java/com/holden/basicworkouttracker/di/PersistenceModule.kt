package com.holden.basicworkouttracker.di

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.holden.basicworkouttracker.MainActivity
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.persistence.LOCAL_EXERCISES
import com.holden.basicworkouttracker.persistence.LOCAL_GROUPS
import com.holden.basicworkouttracker.persistence.loadExercises
import com.holden.basicworkouttracker.persistence.loadGroups
import com.holden.basicworkouttracker.persistence.saveExercises
import com.holden.basicworkouttracker.persistence.saveGroups
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.orderedMapOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Qualifier

data class Persistence(
    val initialExercises: OrderedMap<String, Exercise>,
    val initialGroups: OrderedMap<String, ExerciseGroup>,
    val saveExercises: (CoroutineScope, OrderedMap<String, Exercise>) -> Unit,
    val saveGroups: (CoroutineScope, OrderedMap<String, ExerciseGroup>) -> Unit
)

@Module
@InstallIn(ViewModelComponent::class)
class PersistenceModule {
    @Provides
    fun providePersistence(
        @ApplicationContext context: Context
    ): Persistence = Persistence(
        context.loadExercises(LOCAL_EXERCISES) ?: orderedMapOf(),
        context.loadGroups(LOCAL_GROUPS) ?: orderedMapOf(),
        { scope, exercises ->
            scope.launch(Dispatchers.IO) {
                context.saveExercises(LOCAL_EXERCISES, exercises)
            }
        },
        { scope, groups ->
            scope.launch(Dispatchers.IO) {
                context.saveGroups(LOCAL_GROUPS, groups)
            }

        }
    )
}
