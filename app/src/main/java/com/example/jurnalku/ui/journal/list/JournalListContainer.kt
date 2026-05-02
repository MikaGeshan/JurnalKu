package com.example.jurnalku.ui.journal.list

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun JournalListContainer(
    navController: NavController
) {
    val onNavigateEntries = {
        navController.navigate("entries")
    }

    val onNavigateCreateJournal = {
        navController.navigate("entries")
    }

    fun handleSaveJournal() {
        handleSaveJournal()
    }

    JournalListScreen(
        onNavigateEntries = onNavigateEntries,
        onNavigateCreateJournal = onNavigateCreateJournal

    )
}