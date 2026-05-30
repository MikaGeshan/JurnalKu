package com.example.jurnalku.ui.dateline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.Calendar
import com.example.jurnalku.ui.components.CustomCard
import com.example.jurnalku.ui.components.MoodCounter
import com.example.jurnalku.ui.components.pss.PSSForm
import com.example.jurnalku.ui.components.MoodData
import com.example.jurnalku.ui.components.pss.StressResult
import java.time.LocalDate

@Composable
fun DatelineScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    moodHistory: Map<LocalDate, String>,
    weeklyMoodData: List<MoodData>,
    stressResult: StressResult?,
    latestBatchSize: Int,
    lastPSSDate: Long?,
    lastPSSScore: Int?,
    onPSSScoreCalculated: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Calendar(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
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
                        onScoreCalculated = onPSSScoreCalculated
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
        }
    }
}
