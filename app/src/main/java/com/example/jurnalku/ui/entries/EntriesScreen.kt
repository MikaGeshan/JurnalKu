package com.example.jurnalku.ui.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.stores.AuthStore
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.EmptyStateText
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.SectionTitle

@Composable
fun EntriesScreen(
    selectedMood: MoodClass?,
    onMoodSelected: (MoodClass) -> Unit,
    onNavigateCreateJournal: () -> Unit,
    onLogOut: () -> Unit
) {
    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()

    val firstName = user?.name?.split(" ")?.firstOrNull() ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        // 🔹 HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = "Hi, $firstName 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.bodySmall,
                    color = JungleGreen
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onNavigateCreateJournal) {
                    ComposableIcon(
                        icon = AppIconClass.Add,
                        tint = JungleGreen,
                        size = 26.dp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = { /* mode select nanti aje */ },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey.copy(alpha = 0.6f),
                        contentColor = JungleGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Select",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onLogOut) {
                    ComposableIcon(
                        icon = AppIconClass.Profile,
                        tint = JungleGreen,
                        size = 26.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle("How’s your day?")

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoodClass.all.forEach { mood ->

                val isSelected = selectedMood == mood

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onMoodSelected(mood) }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(if (isSelected) 64.dp else 56.dp)
                    ) {
                        ComposableIcon(
                            icon = mood.icon,
                            tint = Color.Unspecified,
                            size = if (isSelected) 44.dp else 36.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle("My Journal")

        Spacer(modifier = Modifier.height(12.dp))

        EmptyStateText("No journals yet")

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle("Recent Pages")

        Spacer(modifier = Modifier.height(12.dp))

        EmptyStateText("No recent pages")
    }
}