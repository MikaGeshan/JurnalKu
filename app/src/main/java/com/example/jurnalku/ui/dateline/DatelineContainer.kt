package com.example.jurnalku.ui.dateline

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.entries.MoodClass
import com.example.jurnalku.ui.stores.AuthStore
import com.example.jurnalku.ui.stores.MoodStore
import com.google.firebase.firestore.FirebaseFirestore
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
    val monthlyMoodData by moodStore.monthlyMoodData.collectAsState()
    val monthlyMoodValues by moodStore.monthlyMoodValues.collectAsState()
    val lastPSSScore by moodStore.lastPSSScore.collectAsState()
    val lastPSSDate by moodStore.lastPSSDate.collectAsState()
    val stressResult by moodStore.stressResult.collectAsState()
    val latestBatchSize by moodStore.latestBatchSize.collectAsState()
    val isLoading by moodStore.isLoading.collectAsState()

    var activityHistory by remember { mutableStateOf<Map<LocalDate, List<Map<String, Any>>>>(emptyMap()) }

    LaunchedEffect(uid) {
        if (uid != null) {
            moodStore.fetchTodayMood(uid)
            
            // fetch activity log for unified history details
            FirebaseFirestore.getInstance()
                .collection("activity_log")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { result ->
                    val history = result.documents.mapNotNull { doc ->
                        val dateStr = doc.getString("date")
                        val activities = doc.get("activities") as? List<Map<String, Any>>
                        try {
                            val date = LocalDate.parse(dateStr)
                            if (activities != null) date to activities else null
                        } catch (e: Exception) {
                            null
                        }
                    }.toMap()
                    activityHistory = history
                }
        }
    }

    LaunchedEffect(
        selectedDate,
        moodHistoryRaw.hashCode(),
        lastPSSScore,
    ) {
        if ((uid != null) && moodHistoryRaw.isNotEmpty()) {
            val date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            moodStore.calculateMonthlyStats(date)
        }
    }

    val moodHistory = remember(moodHistoryRaw) {
        moodHistoryRaw.mapNotNull { (dateStr, moodKey) ->
            try {
                val date = LocalDate.parse(dateStr)
                val mood = MoodClass.all.find { it.key == moodKey }
                mood?.let { date to it }
            } catch (e: DateTimeParseException) {
                null
            }
        }.toMap()
    }

    DatelineScreen(
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it },
        moodHistory = moodHistory,
        activityHistory = activityHistory,
        monthlyMoodData = monthlyMoodData,
        monthlyMoodValues = monthlyMoodValues,
        stressResult = stressResult,
        latestBatchSize = latestBatchSize,
        lastPSSDate = lastPSSDate,
        lastPSSScore = lastPSSScore,
        isLoading = isLoading,
        onPSSScoreCalculated = { score ->
            uid?.let { moodStore.setPSSScore(it, score) }
        },
    )
}
