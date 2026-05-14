package com.example.jurnalku.ui.entries

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.CustomLoadingSpinner
import com.example.jurnalku.ui.components.PaperTypePreview
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.JournalEntry
import com.example.jurnalku.ui.theme.EmptyStateText
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.SectionTitle

@Composable
fun EntriesScreen(
    uid: String,
    name: String,
    isLoading: Boolean,
    selectedMood: MoodClass?,
    onMoodSelected: (MoodClass) -> Unit,
    onNavigateCreateJournal: () -> Unit,
    getListJournal: (
        String,
        (List<JournalEntry>) -> Unit,
        (Exception) -> Unit
    ) -> Unit,
    onDeleteJournal: (
        String,
        () -> Unit,
        (Exception) -> Unit
    ) -> Unit,
    onEditJournal: (JournalEntry) -> Unit,
    onLogOut: () -> Unit
) {
    var journals by remember {
        mutableStateOf<List<JournalEntry>>(emptyList())
    }

    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {

        getListJournal(
            uid,
            { result ->
                journals = result
            },
            { error ->
                Log.e(
                    "JOURNAL",
                    error.message ?: "ERROR"
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = "Hi, $name 👋",
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
                    onClick = {
                        if (isSelectionMode) {
                            if (selectedIds.isEmpty()) {
                                isSelectionMode = false
                            } else {
                                var deletedCount = 0
                                val totalToDelete = selectedIds.size
                                selectedIds.forEach { id ->
                                    onDeleteJournal(id, {
                                        deletedCount++
                                        if (deletedCount == totalToDelete) {
                                            isSelectionMode = false
                                            selectedIds = emptySet()
                                            refreshTrigger++
                                        }
                                    }, {
                                        Log.e("DELETE", it.message ?: "Error")
                                    })
                                }
                            }
                        } else {
                            isSelectionMode = true
                        }
                    },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelectionMode && selectedIds.isNotEmpty()) Color.Red.copy(alpha = 0.8f) else Grey.copy(alpha = 0.6f),
                        contentColor = if (isSelectionMode && selectedIds.isNotEmpty()) Color.White else JungleGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isSelectionMode) {
                            if (selectedIds.isEmpty()) "Cancel" else "Delete (${selectedIds.size})"
                        } else "Select",
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

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(journals) { journalEntry ->

                val journal = journalEntry.payload
                val isSelected = selectedIds.contains(journalEntry.journalId)

                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isSelectionMode) {
                                if (isSelected) {
                                    selectedIds = selectedIds - journalEntry.journalId
                                } else {
                                    selectedIds = selectedIds + journalEntry.journalId
                                }
                            } else {
                                onEditJournal(journalEntry)
                            }
                        }
                ) {

                    PaperTypePreview(
                        type = journal.paperType,
                        color = Color(journal.paperColor.toULong()),
                        isSelected = isSelected,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .size(24.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ComposableIcon(
                                icon = AppIconClass.Check,
                                tint = JungleGreen,
                                size = 20.dp
                            )
                        }
                    }

                    Text(
                        text = journalEntry.journalName,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (journals.isEmpty()) {

            Spacer(modifier = Modifier.height(16.dp))

            EmptyStateText("No journals yet")
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle("Recent Pages")

        Spacer(modifier = Modifier.height(12.dp))

        EmptyStateText("No recent pages")
    }
        if (isLoading) {
        CustomLoadingSpinner(isOverlay = true)
    }
}