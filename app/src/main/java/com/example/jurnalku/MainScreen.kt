package com.example.jurnalku.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.jurnalku.ui.auth.LoginContainer
import com.example.jurnalku.ui.auth.RegisterContainer
import com.example.jurnalku.ui.components.BottomTabBar
import com.example.jurnalku.ui.dateline.DatelineContainer
import com.example.jurnalku.ui.entries.EntriesContainer
import com.example.jurnalku.ui.journal.create.CreateJournalContainer
import com.example.jurnalku.ui.journal.list.JournalListContainer
import com.example.jurnalku.ui.theme.SoftGreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    val showHeaderRoute = currentRoute == "entries" || currentRoute == "dateline"

    Scaffold(
        containerColor = SoftGreen,
        bottomBar = {
            if (showHeaderRoute) {
                BottomTabBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo("entries")
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {

            composable("login") {
                LoginContainer(
                    onLoginSuccess = {
                        navController.navigate("entries") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterContainer(
                    onRegisterSuccess = {
                        navController.navigate("entries") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                )
            }

            composable("entries") {
                EntriesContainer(navController)
            }

            composable("dateline") {
                DatelineContainer()
            }

            composable("journal_list"){
                JournalListContainer()
            }

            composable("create_journal") {
                CreateJournalContainer(navController)
            }
        }
    }
}