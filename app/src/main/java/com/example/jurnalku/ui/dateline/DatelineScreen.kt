package com.example.jurnalku.ui.dateline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
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
import com.example.jurnalku.ui.theme.*
import java.time.LocalDate

@Composable
fun DatelineScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    moodHistory: Map<LocalDate, com.example.jurnalku.ui.entries.MoodClass>,
    activityHistory: Map<LocalDate, List<Map<String, Any>>>,
    monthlyMoodData: List<MoodData>,
    monthlyMoodValues: List<Int>,
    stressResult: StressResult?,
    latestBatchSize: Int,
    lastPSSDate: Long?,
    lastPSSScore: Int?,
    isLoading: Boolean,
    onPSSScoreCalculated: (Int) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column {
                    Text(
                        text = "Your Journey",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    HorizontalDivider(
                        modifier = Modifier.width(60.dp).padding(top = 4.dp),
                        thickness = 4.dp,
                        color = JungleGreen.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                        moodHistory = moodHistory,
                        activityHistory = activityHistory
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column {
                    Text(
                        text = "Your Mood",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    HorizontalDivider(
                        modifier = Modifier.width(60.dp).padding(top = 4.dp),
                        thickness = 4.dp,
                        color = JungleGreen.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                CustomCard(title = "Monthly Analysis") {
                    MoodCounter(
                        moods = monthlyMoodData,
                        stressResult = stressResult
                    )
                }
            }

            if (latestBatchSize >= 30) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CustomCard(title = "PSS 4 Assessment") {
                        PSSForm(
                            lastTakenDate = lastPSSDate,
                            savedScore = lastPSSScore,
                            moodValues = monthlyMoodValues,
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
