package com.example.jurnalku.ui.splash

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.stores.AuthStore
import kotlinx.coroutines.delay

@Composable
fun SplashContainer(
    onNavigateToEntries: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authStore: AuthStore = viewModel()

    LaunchedEffect(Unit) {
        delay(1500)

        if (authStore.isLoggedIn) {
            onNavigateToEntries()
        } else {
            onNavigateToLogin()
        }
    }

    SplashScreen()
}