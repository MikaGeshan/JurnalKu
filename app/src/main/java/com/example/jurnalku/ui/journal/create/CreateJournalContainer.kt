package com.example.jurnalku.ui.journal.create

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import java.util.UUID
import com.example.jurnalku.ui.journal.list.JournalPayload
import com.example.jurnalku.ui.journal.list.DrawPathPayload
import com.example.jurnalku.ui.journal.list.DrawPointPayload
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.JournalRepository
import com.example.jurnalku.ui.stores.AuthStore

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CreateJournalContainer(
    navController: NavController
) {

    val repository = remember { JournalRepository() }

    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    val onBackToEntries = {
        navController.navigate("entries")
    }

    val onCancelCreateJournal = {
        navController.navigate("journal_list")
    }

    fun generateJournalPayload(
        text: String,
        paperType: String,
        paperColor: Color,
        paths: List<DrawPath>,
        imageUri: String?,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageScale: Float,
        imageRotation: Float
    ): JournalPayload {

        return JournalPayload(
            contentId = UUID.randomUUID().toString(),
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
                    points = path.points.map {
                        DrawPointPayload(
                            x = it.x,
                            y = it.y
                        )
                    },
                    color = path.color.value.toLong(),
                    strokeWidth = path.strokeWidth
                )
            }
        )
    }

    fun handleSaveJournal(
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

        val payload = generateJournalPayload(
            text = text,
            paperType = paperType,
            paperColor = paperColor,
            paths = paths,
            imageUri = imageUri,
            imageOffsetX = imageOffsetX,
            imageOffsetY = imageOffsetY,
            imageScale = imageScale,
            imageRotation = imageRotation
        )

        val uid = user?.uid ?: return

        repository.saveJournal(
            uid = uid,
            payload = payload,

            onSuccess = {
                Log.d("FIRESTORE", "SAVE SUCCESS")
                showSuccessDialog = true
            },

            onError = {
                Log.e(
                    "FIRESTORE",
                    it.message ?: "UNKNOWN ERROR"
                )
            }
        )
    }

    CreateJournalScreen(
        onCancelCreateJournal = onCancelCreateJournal,
        onBackToEntries = onBackToEntries,
        onSave = ::handleSaveJournal
    )

    if (showSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { /* Don't dismiss by clicking outside */ },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("entries") {
                            popUpTo("entries") { inclusive = true }
                        }
                    }
                ) {
                    androidx.compose.material3.Text("OK")
                }
            },
            title = { androidx.compose.material3.Text("Success") },
            text = { androidx.compose.material3.Text("Your journal has been created successfully!") }
        )
    }
}
