package com.example.jurnalku.ui.entries

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavController

@Composable
fun EntriesContainer(
    navController: NavController
) {

    val onNavigateCreateJournal = {
        navController.navigate("create_journal")
    }

    var selectedMood by remember { mutableStateOf<MoodClass?>(null) }

    fun handleMoodSelect(mood: MoodClass) {
        selectedMood = mood

        val payload = mapOf(
            "mood" to mood.key
        )

        Log.d("=== mood payload", payload.toString())
    }

    EntriesScreen(
        selectedMood = selectedMood,
        onMoodSelected = ::handleMoodSelect,
        onNavigateCreateJournal = onNavigateCreateJournal
    )
}