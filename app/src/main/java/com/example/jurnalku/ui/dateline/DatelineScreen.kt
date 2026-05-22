package com.example.jurnalku.ui.dateline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    val weeklyAverageMood by moodStore.weeklyAverageMood.collectAsState()
    val weeklyStressScore by moodStore.weeklyStressScore.collectAsState()
    val lastPSSScore by moodStore.lastPSSScore.collectAsState()
    val lastPSSDate by moodStore.lastPSSDate.collectAsState()
    val finalStressScore by moodStore.finalStressScore.collectAsState()
    val stressResult by moodStore.stressResult.collectAsState()

    LaunchedEffect(uid) {
        uid?.let { moodStore.fetchTodayMood(it) }
    }

    LaunchedEffect(selectedDate, moodHistoryRaw, lastPSSScore) {
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

    val totalMoods = moodHistory.size
    val canTakePSS = totalMoods >= 30

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
                averageMood = weeklyAverageMood
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            if (finalStressScore != null) {
                CustomCard(title = "Overall Stress Insight") {
                    Column {
                        Text(
                            text = stressResult?.stressLevel ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = when(stressResult?.stressLevel) {
                                "Stress Rendah" -> Green
                                "Stress Sedang" -> Orange
                                "Stress Tinggi" -> Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Combined weekly stress level:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "%.2f / 16.0".format(finalStressScore),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Calculation: (PSS Score: $lastPSSScore + Mood Stress: %.2f) / 2".format(weeklyStressScore),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                CustomCard(title = "Weekly Stress (Mood Based)") {
                    Column {
                        Text(
                            text = "Your average mood-based stress score for this week is:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "%.2f / 16.0".format(weeklyStressScore),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (canTakePSS){
                            Text(
                                text = "Complete the PSS-4 below to see your combined stress insight.",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            
            var forceShowResults by remember { mutableStateOf(false) }

            if (canTakePSS || forceShowResults) {
                CustomCard(title = "Perceived Stress Scale (PSS-4)") {
                    PSSForm(
                        lastTakenDate = lastPSSDate,
                        onScoreCalculated = { score ->
                            uid?.let { moodStore.setPSSScore(it, score) }
                            forceShowResults = true
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
