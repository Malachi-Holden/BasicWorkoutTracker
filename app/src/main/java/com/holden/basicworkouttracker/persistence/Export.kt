package com.holden.basicworkouttracker.persistence

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile
import com.holden.basicworkouttracker.R
import com.holden.basicworkouttracker.exercise.Exercise
import com.holden.basicworkouttracker.exercise.ExerciseGroup
import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.orderedMapOf
import com.holden.basicworkouttracker.util.toPairs
import kotlinx.serialization.Serializable
import java.io.IOException

@Serializable
data class BWTData(
    val groups: List<Pair<String, ExerciseGroup>>,
    val exercises: List<Pair<String, Exercise>>,
    val plates: Pair<Double, List<Double>>
) {
    constructor(
        groups: OrderedMap<String, ExerciseGroup>,
        exercises: OrderedMap<String, Exercise>,
        plates: Pair<Double, List<Double>>
    ): this(groups.toPairs(), exercises.toPairs(), plates)
}

class BWTSaveError(message: String): IOException(message)

fun saveDataToUri(context: Context, uri: Uri, fileName: String, bwtData: BWTData) {
    val contentResolver = context.contentResolver
    val file = DocumentFile.fromTreeUri(context, uri)
    val newUri = file?.uri ?: throw BWTSaveError(context.getString(R.string.no_file_found, uri.toString()))
    file.findFile(fileName)?.let { previous ->
        DocumentsContract.deleteDocument(contentResolver, previous.uri)
    }

    val documentUri = DocumentsContract.createDocument(
        contentResolver, newUri, "application/json", fileName
    ) ?: return run {
        Toast.makeText(
            context,
            context.getString(R.string.null_file_uri),
            Toast.LENGTH_SHORT
        ).show()
    }
    contentResolver.openOutputStream(documentUri)?.use { outputStream ->
        saveBWTObject(outputStream, bwtData)
    }
}

fun loadDataFromUri(context: Context, uri: Uri): BWTData? {
    val contentResolver = context.contentResolver
    val file = DocumentFile.fromSingleUri(context, uri)
    file?.uri ?: throw BWTSaveError(context.getString(R.string.no_file_found, uri.toString()))
    return contentResolver.openInputStream(uri)?.use { inputStream ->
        loadBWTObject<BWTData>(inputStream)
    }
}

@Composable
fun buildSaveBWTAction(
    getData: Context.() -> BWTData = {
        BWTData(
            loadGroups(LOCAL_GROUPS) ?: orderedMapOf(),
            loadExercises(LOCAL_EXERCISES) ?: orderedMapOf(),
            loadPlates(LOCAL_PLATES) ?: (45.0 to listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5))
        )
    },
    fileName: String = stringResource(id = R.string.save_file)
): () -> Unit {
    val context = LocalContext.current
    val errorMessage = stringResource(id = R.string.choose_file_error)
    return buildChooseFolderAction { uri ->
        val data = context.getData()
        val resolvedUri = uri ?: return@buildChooseFolderAction run {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
        try {
            saveDataToUri(context, resolvedUri, fileName, data)
        } catch (e: BWTSaveError) {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


@Composable
fun buildLoadBWTFromSaveAction(onLoaded: (BWTData?) -> Unit): () -> Unit {
    val context = LocalContext.current
    return buildChooseFileAction { uri ->
        val resolvedUri = uri ?: return@buildChooseFileAction run {
            Toast.makeText(
                context,
                R.string.choose_file_error,
                Toast.LENGTH_SHORT
            ).show()
        }
        try {
            onLoaded(loadDataFromUri(context, resolvedUri))
        } catch (e: BWTSaveError) {

            onLoaded(null)
        }
    }
}

@Composable
fun buildChooseFolderAction(onFileChosen: (Uri?) -> Unit): () -> Unit {
    val pickFolderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(), onFileChosen
    )
    val context = LocalContext.current
    val extDir = Uri.fromFile(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))
    return { pickFolderLauncher.launch(extDir) }
}

@Composable
fun buildChooseFileAction(onFileChosen: (Uri?) -> Unit): () -> Unit {
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(), onFileChosen
    )
    return { pickFileLauncher.launch("*/*") }
}

@Preview
@Composable
fun ChooseFolder() {
    val action = buildSaveBWTAction({
        BWTData(orderedMapOf(), orderedMapOf(), 45.0 to listOf())
    })
    Column {
        Button(onClick = action) {
            Text(text = "choose file")
        }
    }
}

@Preview
@Composable
fun LoadFromFile() {
    var bwtData by remember {
        mutableStateOf<BWTData?>(null)
    }
    val action = buildLoadBWTFromSaveAction { data ->
        bwtData = data
    }
    Column {
        Button(onClick = action) {
            Text(text = "choose file")
        }
        Text(text = bwtData.toString())
    }
}
