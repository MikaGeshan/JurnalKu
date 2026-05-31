package com.example.jurnalku.ui.dateline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.Calendar
import com.example.jurnalku.ui.components.CustomCard
import com.example.jurnalku.ui.components.CustomLoadingSpinner
import com.example.jurnalku.ui.components.MoodCounter
import com.example.jurnalku.ui.components.pss.PSSForm
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.MoodData
import com.example.jurnalku.ui.components.pss.StressResult
import java.time.LocalDate

@Composable
fun DatelineScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    moodHistory: Map<LocalDate, AppIconClass>,
    weeklyMoodData: List<MoodData>,
    stressResult: StressResult?,
    latestBatchSize: Int,
    lastPSSDate: Long?,
    lastPSSScore: Int?,
    isLoading: Boolean,
    onPSSScoreCalculated: (Int) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Your Journey",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Tracking your moods and growth",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            CustomCard(title = "Calendar") {
                Calendar(
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    moodHistory = moodHistory
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            CustomCard(title = "Weekly Analysis") {
                MoodCounter(
                    moods = weeklyMoodData,
                    stressResult = stressResult
                )
            }
        }

        if (latestBatchSize >= 30) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                CustomCard(title = "Stress Scale (PSS-4)") {
                    PSSForm(
                        lastTakenDate = lastPSSDate,
                        savedScore = lastPSSScore,
                        onScoreCalculated = onPSSScoreCalculated
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }

    if (isLoading) {
        CustomLoadingSpinner(isOverlay = true)
    }
    }
}
