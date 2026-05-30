package com.example.jurnalku.ui.dateline

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.entries.MoodClass
import com.example.jurnalku.ui.stores.AuthStore
import com.example.jurnalku.ui.stores.MoodStore
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.util.Date

@Composable
fun DatelineContainer() {
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
        lastPSSScore,
    ) {
        if ((uid != null) && moodHistoryRaw.isNotEmpty()) {
            val date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            moodStore.calculateWeeklyStats(date)
        }
    }

    val moodHistory = remember(moodHistoryRaw) {
        moodHistoryRaw.mapNotNull { (dateStr, moodKey) ->
            try {
                val date = LocalDate.parse(dateStr)
                val moodIcon = MoodClass.all.find { it.key == moodKey }?.icon
                moodIcon?.let { date to it }
            } catch (e: DateTimeParseException) {
                null
            }
        }.toMap()
    }

    DatelineScreen(
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it },
        moodHistory = moodHistory,
        weeklyMoodData = weeklyMoodData,
        stressResult = stressResult,
        latestBatchSize = latestBatchSize,
        lastPSSDate = lastPSSDate,
        lastPSSScore = lastPSSScore,
        onPSSScoreCalculated = { score ->
            uid?.let { moodStore.setPSSScore(it, score) }
        },
    )
}
