package com.example.jurnalku.ui.entries

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.stores.AuthStore

@Composable
fun EntriesContainer(
    navController: NavController,
    onNavigateToLogin: () -> Unit
) {

    val authStore : AuthStore = viewModel()

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

    fun handleLogout() {
        Log.d("LOG_OUT", "logout")

        authStore.logout()

        navController.navigate("login") {
            popUpTo(0)
        }
    }

    EntriesScreen(
        selectedMood = selectedMood,
        onMoodSelected = ::handleMoodSelect,
        onNavigateCreateJournal = onNavigateCreateJournal,
        onLogOut = ::handleLogout
    )
}