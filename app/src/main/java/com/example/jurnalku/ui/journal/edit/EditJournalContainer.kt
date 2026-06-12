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
import com.example.jurnalku.ui.journal.list.RecentPageEntry
import com.example.jurnalku.ui.journal.list.toRecentPageEntry
import com.example.jurnalku.ui.stores.AuthStore
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EditJournalContainer(
    journalId: String,
    navController: NavController
) {
    val repository = remember { JournalRepository() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()

    var pages by remember { mutableStateOf<List<JournalPagePayload>>(emptyList()) }
    var journalName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(journalId, user) {
        val uid = user?.uid ?: return@LaunchedEffect
        repository.getJournal(
            journalId = journalId,
            onSuccess = { journal ->
                pages = journal.pages
                journalName = journal.journalName
                isLoading = false

                // Save first page as recent for now
                if (journal.pages.isNotEmpty()) {
                    repository.saveRecentPage(
                        context = context,
                        uid = uid,
                        recentPage = journal.pages.first().toRecentPageEntry(
                            journalId = journalId,
                            journalName = journal.journalName,
                            pageIndex = 0
                        )
                    )
                }
            },
            onError = {
                Log.e("EDIT_JOURNAL", it.message ?: "Error")
                isLoading = false
            }
        )
    }

    fun handleSave(updatedPages: List<JournalPagePayload>) {
        val uid = user?.uid ?: return
        repository.updateJournal(
            uid = uid,
            journalId = journalId,
            journalName = journalName,
            pages = updatedPages,
            onSuccess = {
                // Update recent page info when saving
                if (updatedPages.isNotEmpty()) {
                    repository.saveRecentPage(
                        context = context,
                        uid = uid,
                        recentPage = updatedPages.first().toRecentPageEntry(
                            journalId = journalId,
                            journalName = journalName,
                            pageIndex = 0
                        )
                    )
                }
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
        if (pages.isNotEmpty()) {
            EditJournalScreen(
                pages = pages,
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
