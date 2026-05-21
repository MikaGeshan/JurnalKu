package com.example.jurnalku.ui.dateline

import com.example.jurnalku.ui.components.MoodCounter
import com.example.jurnalku.ui.components.MoodData
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.components.Calendar
import com.example.jurnalku.ui.components.CustomCard
import com.example.jurnalku.ui.components.pss.PSSForm
import com.example.jurnalku.ui.entries.MoodClass
import com.example.jurnalku.ui.stores.MoodStore
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Red
import com.example.jurnalku.ui.theme.Yellow
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Composable
fun DatelineScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val moodStore: MoodStore = viewModel()
    val moodHistoryRaw by moodStore.moodHistory.collectAsState()

    // Map the raw history (String: String) to (LocalDate: Emoji)
    val moodHistory = remember(moodHistoryRaw) {
        moodHistoryRaw.mapNotNull { (dateStr, moodKey) ->
            try {
                val date = LocalDate.parse(dateStr)
                val emoji = MoodClass.getEmoji(moodKey)
                if (emoji != null) date to emoji else null
            } catch (e: DateTimeParseException) {
                null
            }
        }.toMap()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Calendar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                moodHistory = moodHistory
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            MoodCounter(
                moods = listOf(
                    MoodData("Good", 10, Green),
                    MoodData("Meh", 5, Yellow),
                    MoodData("Awful", 2, Red)
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            CustomCard(title = "Monthly Stress Level") {
                PSSForm()
            }
        }
    }
}
