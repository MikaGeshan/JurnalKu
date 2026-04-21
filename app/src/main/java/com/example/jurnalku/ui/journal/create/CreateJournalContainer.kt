package com.example.jurnalku.ui.journal.create

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CreateJournalContainer(navController: NavController)  {
    val onCancelCreateJournal = {
        navController.navigate("journal_list")
    }
        CreateJournalScreen(onCancelCreateJournal)
    }