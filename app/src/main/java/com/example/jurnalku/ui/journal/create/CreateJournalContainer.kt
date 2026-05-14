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
import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.JournalEntry
import com.example.jurnalku.ui.journal.list.DrawPathPayload
import com.example.jurnalku.ui.journal.list.DrawPointPayload
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.JournalRepository
import com.example.jurnalku.ui.journal.list.RecentPageEntry
import com.example.jurnalku.ui.stores.AuthStore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CreateJournalContainer(
    navController: NavController
) {

    val repository = remember { JournalRepository() }

    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showCanvas by remember { mutableStateOf(false) }
    var journalName by remember { mutableStateOf("") }

    val onBackToEntries = {
        if (showCanvas) {
            showCanvas = false
            journalName = ""
        } else {
            navController.navigate("entries")
        }
    }

    val onCancelCreateJournal = {
        if (showCanvas) {
            showCanvas = false
            journalName = ""
        } else {
            navController.navigate("journal_list")
        }
    }

    fun generateJournalPayload(
        text: String,
        paperType: String,
        paperColor: Color,
        paths: List<DrawPath>,
        imageBase64: String?,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageScale: Float,
        imageRotation: Float
    ): JournalPagePayload {

        return JournalPagePayload(
            contentId = UUID.randomUUID().toString(),
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

    fun handleSaveJournal(pages: List<JournalPagePayload>) {
        val uid = user?.uid ?: return

        val journalEntry = JournalEntry(
            journalId = UUID.randomUUID().toString(),
            journalName = journalName,
            pages = pages
        )

        repository.saveJournal(
            uid = uid,
            journalEntry = journalEntry,

            onSuccess = {
                Log.d("FIRESTORE", "SAVE SUCCESS")

                // Save as recent page
                if (pages.isNotEmpty()) {
                    val firstPage = pages.first()
                    repository.saveRecentPage(
                        uid = uid,
                        recentPage = RecentPageEntry(
                            journalId = journalEntry.journalId,
                            journalName = journalEntry.journalName,
                            pageIndex = 0,
                            paperType = firstPage.paperType,
                            paperColor = firstPage.paperColor
                        )
                    )
                }

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
        showCanvas = showCanvas,
        onPaperSelected = { type, color ->
            showNameDialog = true
        },
        onSave = ::handleSaveJournal
    )

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Enter Journal Name") },
            text = {
                Column {
                    Text("Please provide a name for your journal.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = journalName,
                        onValueChange = { journalName = it },
                        placeholder = { Text("My Journal") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (journalName.isNotBlank()) {
                            showNameDialog = false
                            showCanvas = true
                        }
                    },
                    enabled = journalName.isNotBlank()
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss by clicking outside */ },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("entries") {
                            popUpTo("entries") { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text("Your journal has been created successfully!") }
        )
    }
}
