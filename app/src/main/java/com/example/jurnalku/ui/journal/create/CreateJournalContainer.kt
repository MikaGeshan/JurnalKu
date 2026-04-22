package com.example.jurnalku.ui.journal.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CreateJournalContainer(navController: NavController)  {

    val onBackToEntries = {
        navController.navigate("entries")
    }

    val onCancelCreateJournal = {
        navController.navigate("journal_list")
    }
        CreateJournalScreen(
            onCancelCreateJournal = onCancelCreateJournal,
            onBackToEntries = onBackToEntries
        )
    }