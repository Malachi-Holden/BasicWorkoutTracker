package com.holden.basicworkouttracker.persistence

import android.content.Context
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.div
import com.holden.basicworkouttracker.util.toOrderedMap
import com.holden.basicworkouttracker.util.toPairs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


val LOCAL_EXERCISES = "LOCAL_EXERCISES"
val LOCAL_PLATES = "LOCAL_PLATES"
val LOCAL_GROUPS = "LOCAL_GROUPS"

fun Context.loadGroups(groupsFile: String): OrderedMap<String, ExerciseGroup>?
= loadBWTObject<List<Pair<String, ExerciseGroup>>>(groupsFile)
    ?.toOrderedMap()

fun Context.loadExercises(exercisesFile: String): OrderedMap<String, Exercise>?
= loadBWTObject<List<Pair<String, Exercise>>>(exercisesFile)
    ?.toOrderedMap()

fun Context.loadPlates(platesFile: String): Pair<Double, List<Double>>? = loadBWTObject(platesFile)

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

fun Context.saveGroups(groupsFile: String, groups: OrderedMap<String, ExerciseGroup>) {
    saveBWTObject(groupsFile, groups.toPairs())
}

fun Context.saveExercises(exercisesFile: String, exercises: OrderedMap<String, Exercise>) {
    saveBWTObject(exercisesFile, exercises.toPairs())
}

fun Context.savePlates(platesFile: String, plates: List<Double>, bar: Double) {
    saveBWTObject(platesFile, bar to plates)
}

inline fun <reified O>Context.saveBWTObject(appFile: String, bwtObject: O) {
    val file = filesDir / appFile
    file.createNewFile()
    saveBWTObject(file.outputStream(), bwtObject)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified O>saveBWTObject(stream: OutputStream, bwtObject: O) {
    try {
        Json.encodeToStream(bwtObject, stream)
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: SerializationException) {
        e.printStackTrace()
    }
}

inline fun <reified O>Context.loadBWTObject(appFile: String): O? {
    return loadBWTObject(fileInputStream(filesDir / appFile) ?: return null)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified O>loadBWTObject(stream: InputStream): O? {
    return try {
        Json.decodeFromStream(stream)
    } catch (e: SerializationException) {
        e.printStackTrace()
        null
    }
}