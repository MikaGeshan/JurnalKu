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
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

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
        imageBase64: String?,
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
            imageBase64 = imageBase64,
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
                showUpdateSuccessDialog = true
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

    if (showUpdateSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { /* Don't dismiss by clicking outside */ },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showUpdateSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    androidx.compose.material3.Text("OK")
                }
            },
            title = { androidx.compose.material3.Text("Updated") },
            text = { androidx.compose.material3.Text("Your journal has been updated successfully!") }
        )
    }
}
