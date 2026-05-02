package com.example.jurnalku.ui.journal.create

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.JournalRepository
import com.example.jurnalku.ui.journal.list.generateJournalPayload
import com.example.jurnalku.ui.stores.AuthStore

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CreateJournalContainer(
    navController: NavController
) {

    val repository = remember { JournalRepository() }

    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()

    val onBackToEntries = {
        navController.navigate("entries")
    }

    val onCancelCreateJournal = {
        navController.navigate("journal_list")
    }

    fun handleSaveJournal(
        text: String,
        paths: List<DrawPath>,
        paperType: String,
        paperColor: Color
    ) {

        val payload = generateJournalPayload(
            text = text,
            paperType = paperType,
            paperColor = paperColor,
            paths = paths
        )

        val uid = user?.uid ?: return

        repository.saveJournal(
            uid = uid,
            payload = payload,

            onSuccess = {
                Log.d("FIRESTORE", "SAVE SUCCESS")

                navController.navigate("entries")
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
}