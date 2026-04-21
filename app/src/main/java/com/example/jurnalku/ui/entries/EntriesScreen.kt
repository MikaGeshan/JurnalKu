package com.example.jurnalku.ui.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun EntriesScreen(
    selectedMood: MoodClass?,
    onMoodSelected: (MoodClass) -> Unit,
    onNavigateCreateJournal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Hi, User",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = {onNavigateCreateJournal() }) {
                    ComposableIcon(
                        icon = AppIconClass.Add,
                        tint = JungleGreen,
                        size = 28.dp
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey,
                        contentColor = Black
                    )
                ) {
                    Text("Select", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { }) {
                    ComposableIcon(
                        icon = AppIconClass.Profile,
                        tint = JungleGreen,
                        size = 28.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // select mood
        Text(
            text = "How’s your day?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoodClass.all.forEach { mood ->

                val isSelected = selectedMood == mood

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onMoodSelected(mood) }
                ) {
                    ComposableIcon(
                        icon = mood.icon,
                        tint = Color.Unspecified,
                        size = if (isSelected) 44.dp else 36.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // journal list
        Text(
            "My Journal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "No journals yet",
            style = MaterialTheme.typography.bodyMedium,
            color = Grey
        )

        Spacer(modifier = Modifier.height(24.dp))

        // recent opened pages
        Text(
            "Recent Pages",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "No recent pages",
            style = MaterialTheme.typography.bodyMedium,
            color = Grey
        )
    }
}