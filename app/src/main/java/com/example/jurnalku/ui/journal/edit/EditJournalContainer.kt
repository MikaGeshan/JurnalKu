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
import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.JournalRepository

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EditJournalContainer(
    journalId: String,
    navController: NavController
) {
    val repository = remember { JournalRepository() }
    var journalPayload by remember { mutableStateOf<JournalPagePayload?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(journalId) {
        repository.getJournal(
            journalId = journalId,
            onSuccess = {
                journalPayload = it.payload
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
        val currentPayload = journalPayload ?: return
        
        val updatedPayload = currentPayload.copy(
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
            journalId = journalId,
            payload = updatedPayload,
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
        journalPayload?.let {
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
