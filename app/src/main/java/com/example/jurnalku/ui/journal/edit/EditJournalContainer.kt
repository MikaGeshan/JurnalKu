package com.example.jurnalku.ui.journal.edit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.DrawPathPayload
import com.example.jurnalku.ui.journal.list.DrawPointPayload
import com.example.jurnalku.ui.journal.list.JournalPayload
import com.example.jurnalku.ui.journal.list.JournalRepository

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EditJournalContainer(
    contentId: String,
    navController: NavController
) {
    val repository = remember { JournalRepository() }
    var journal by remember { mutableStateOf<JournalPayload?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(contentId) {
        repository.getJournal(
            contentId = contentId,
            onSuccess = {
                journal = it
                isLoading = false
            },
            onError = {
                Log.e("EDIT_JOURNAL", it.message ?: "Error")
                isLoading = false
            }
        )
    }

    fun handleSave(
        text: String,
        paths: List<DrawPath>,
        paperType: String,
        paperColor: Color,
        imageUri: String?,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageScale: Float,
        imageRotation: Float
    ) {
        val currentJournal = journal ?: return
        
        val updatedJournal = currentJournal.copy(
            text = text,
            paperType = paperType,
            paperColor = paperColor.value.toLong(),
            imageUri = imageUri,
            imageOffsetX = imageOffsetX,
            imageOffsetY = imageOffsetY,
            imageScale = imageScale,
            imageRotation = imageRotation,
            paths = paths.map { path ->
                DrawPathPayload(
                    points = path.points.map { DrawPointPayload(it.x, it.y) },
                    color = path.color.value.toLong(),
                    strokeWidth = path.strokeWidth
                )
            }
        )

        repository.updateJournal(
            contentId = contentId,
            payload = updatedJournal,
            onSuccess = {
                navController.popBackStack()
            },
            onError = {
                Log.e("EDIT_JOURNAL", it.message ?: "Update Error")
            }
        )
    }

    if (isLoading) {
        // You can add a loading spinner here
    } else {
        journal?.let {
            EditJournalScreen(
                journal = it,
                onBack = { navController.popBackStack() },
                onSave = ::handleSave
            )
        }
    }
}
