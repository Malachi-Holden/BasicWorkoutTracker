package com.holden.basicworkouttracker

import android.content.Context
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.div
import com.holden.basicworkouttracker.util.toOrderedMap
import com.holden.basicworkouttracker.util.toPairs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.IOException
import java.io.InputStream


val LOCAL_EXERCISES = "LOCAL_EXERCISES"
val LOCAL_PLATES = "LOCAL_PLATES"


@OptIn(ExperimentalSerializationApi::class)
fun Context.loadExercises(exercisesFile: String): OrderedMap<String, Exercise>? {
    val stream = fileInputStream(filesDir / exercisesFile) ?: return null
    val exerciseList: List<Pair<String, Exercise>> = try {
        Json.decodeFromStream(stream)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return null
    }
    return exerciseList.toOrderedMap()
}

@OptIn(ExperimentalSerializationApi::class)
fun Context.loadPlates(platesFile: String): Pair<Double, List<Double>>? {
    val stream = fileInputStream(filesDir / platesFile) ?: return null

    return try {
        Json.decodeFromStream(stream)
    } catch (e: SerializationException) {
        e.printStackTrace()
        null
    }
}

fun fileInputStream(file: File): InputStream? {
    return try {
        File(file.absolutePath).inputStream()
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } catch (e: SerializationException) {
        e.printStackTrace()
        return null
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Context.saveExercises(exercisesFile: String, exercises: OrderedMap<String, Exercise>) {
    val file = (filesDir / exercisesFile).apply { createNewFile() }
    try {
        Json.encodeToStream(exercises.toPairs(), file.outputStream())
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: SerializationException) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Context.savePlates(platesFile: String, plates: List<Double>, bar: Double) {
    val file = (filesDir / platesFile).apply { createNewFile() }
    try {
        Json.encodeToStream(bar to plates, file.outputStream())
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: SerializationException) {
        e.printStackTrace()
    }
}
