package com.example.jurnalku.ui.dateline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.components.Calendar
import com.example.jurnalku.ui.components.CustomCard
import com.example.jurnalku.ui.components.MoodCounter
import com.example.jurnalku.ui.components.pss.PSSForm
import com.example.jurnalku.ui.entries.MoodClass
import com.example.jurnalku.ui.stores.MoodStore
import com.example.jurnalku.ui.stores.AuthStore
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Orange
import com.example.jurnalku.ui.theme.Red
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.util.Date

@Composable
fun DatelineScreen() {
    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()
    val uid = user?.uid

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val moodStore: MoodStore = viewModel()

    val moodHistoryRaw by moodStore.moodHistory.collectAsState()
    val weeklyMoodData by moodStore.weeklyMoodData.collectAsState()
    val lastPSSScore by moodStore.lastPSSScore.collectAsState()
    val lastPSSDate by moodStore.lastPSSDate.collectAsState()
    val stressResult by moodStore.stressResult.collectAsState()
    val latestBatchSize by moodStore.latestBatchSize.collectAsState()

    LaunchedEffect(uid) {
        uid?.let { moodStore.fetchTodayMood(it) }
    }

    LaunchedEffect(
        selectedDate,
        moodHistoryRaw.hashCode(),
        lastPSSScore
    ) {
        if (uid != null && moodHistoryRaw.isNotEmpty()) {
            val date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            moodStore.calculateWeeklyStats(date)
        }
    }


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
                moods = weeklyMoodData,
                stressResult = stressResult
            )
        }

        if (latestBatchSize >= 30) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                CustomCard(title = "Perceived Stress Scale (PSS-4)") {
                    PSSForm(
                        lastTakenDate = lastPSSDate,
                        savedScore = lastPSSScore,
                        onScoreCalculated = { score ->
                            uid?.let { moodStore.setPSSScore(it, score) }
                        }
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
        }
    }
}
