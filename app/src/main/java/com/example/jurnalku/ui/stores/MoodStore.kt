package com.example.jurnalku.ui.stores

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.jurnalku.ui.components.MoodData
import com.example.jurnalku.ui.components.pss.StressCalculator
import com.example.jurnalku.ui.components.pss.StressResult
import com.example.jurnalku.ui.entries.MoodClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class MoodStore(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    private val _selectedMood = MutableStateFlow<MoodClass?>(null)
    val selectedMood: StateFlow<MoodClass?> = _selectedMood

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _moodHistory = MutableStateFlow<Map<String, String>>(emptyMap())
    val moodHistory: StateFlow<Map<String, String>> = _moodHistory

    private val _weeklyMoodData = MutableStateFlow<List<MoodData>>(emptyList())
    val weeklyMoodData: StateFlow<List<MoodData>> = _weeklyMoodData

    private val _weeklyStressScore = MutableStateFlow(0.0)
    val weeklyStressScore: StateFlow<Double> = _weeklyStressScore

    private val _lastPSSScore = MutableStateFlow<Int?>(null)
    val lastPSSScore: StateFlow<Int?> = _lastPSSScore

    private val _finalStressScore = MutableStateFlow<Double?>(null)
    val finalStressScore: StateFlow<Double?> = _finalStressScore

    private val _stressResult = MutableStateFlow<StressResult?>(null)
    val stressResult: StateFlow<StressResult?> = _stressResult

    fun setPSSScore(score: Int) {
        _lastPSSScore.value = score
        updateFinalStressScore()
    }

    private fun updateFinalStressScore() {
        val pssScore = _lastPSSScore.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val moodValues = mutableListOf<Int>()
        repeat(7) {
            val dateStr = sdf.format(cal.time)
            val moodKey = _moodHistory.value[dateStr]
            MoodClass.all.find { it.key == moodKey }?.let {
                moodValues.add(it.value)
            }
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }

        if (pssScore != null) {
            val result = StressCalculator.calculate(pssScore, moodValues)
            _stressResult.value = result
            _finalStressScore.value = result.finalScore
            _weeklyStressScore.value = result.convertedMood
        }
    }

    private fun calculateWeeklyStats() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val weeklyMoods = mutableListOf<MoodClass>()
        
        repeat(7) {
            val dateStr = sdf.format(cal.time)
            val moodKey = _moodHistory.value[dateStr]
            MoodClass.all.find { it.key == moodKey }?.let {
                weeklyMoods.add(it)
            }
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }

        val moodCounts = MoodClass.all.map { mood ->
            MoodData(
                label = mood.key,
                count = weeklyMoods.count { it.key == mood.key },
                color = mood.color,
                emoji = mood.emoji
            )
        }
        _weeklyMoodData.value = moodCounts

        val moodValues = weeklyMoods.map { it.value }
        if (moodValues.isNotEmpty()) {
            val avg = StressCalculator.calculateAverageMood(moodValues)
            _weeklyStressScore.value = StressCalculator.convertMoodToPSSScale(avg)
        } else {
            _weeklyStressScore.value = 0.0
        }

        updateFinalStressScore()
    }

    private fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun fetchTodayMood(uid: String) {
        _isLoading.value = true
        db.collection("mood_entries")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fullHistory = mutableMapOf<String, String>()
                querySnapshot.documents.forEach { doc ->
                    val moods = doc.get("moods") as? Map<String, Long> ?: emptyMap()
                    moods.forEach { (date, value) ->
                        val moodKey = MoodClass.all.find { it.value == value.toInt() }?.key
                        if (moodKey != null) {
                            fullHistory[date] = moodKey
                        }
                    }
                }
                _moodHistory.value = fullHistory
                calculateWeeklyStats()
                
                val today = getTodayDate()
                _selectedMood.value = MoodClass.all.find { it.key == fullHistory[today] }
                _isLoading.value = false
                Log.d("MoodStore", "History fetched from Firestore: ${fullHistory.size} entries")
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun saveMood(
        uid: String,
        mood: MoodClass,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val today = getTodayDate()
        _isLoading.value = true

        // Fetch all batch documents for this user
        db.collection("mood_entries")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Find the latest document by sorting in memory to avoid index requirements
                val latestDoc = querySnapshot.documents
                    .sortedByDescending { it.getLong("created_at") ?: 0L }
                    .firstOrNull()

                val moods = (latestDoc?.get("moods") as? Map<String, Long>)?.mapValues { it.value.toInt() }?.toMutableMap() ?: mutableMapOf()

                // Check if we should update the existing latest batch or create a new one
                if (latestDoc != null && (moods.containsKey(today) || moods.size < 7)) {
                    moods[today] = mood.value
                    latestDoc.reference.update(
                        "moods", moods,
                        "last_updated", System.currentTimeMillis()
                    ).addOnSuccessListener {
                        Log.d("MoodStore", "Mood updated in existing batch")
                        _selectedMood.value = mood
                        fetchTodayMood(uid) 
                        _isLoading.value = false
                        onSuccess()
                    }.addOnFailureListener {
                        Log.e("MoodStore", "Failed to update mood", it)
                        _isLoading.value = false
                        onError(it)
                    }
                } else {
                    // Create a new batch document
                    val newPayload = mapOf(
                        "uid" to uid,
                        "moods" to mapOf(today to mood.value),
                        "created_at" to System.currentTimeMillis(),
                        "last_updated" to System.currentTimeMillis()
                    )
                    db.collection("mood_entries").add(newPayload)
                        .addOnSuccessListener {
                            Log.d("MoodStore", "New mood batch created")
                            _selectedMood.value = mood
                            fetchTodayMood(uid)
                            _isLoading.value = false
                            onSuccess()
                        }.addOnFailureListener {
                            Log.e("MoodStore", "Failed to create new batch", it)
                            _isLoading.value = false
                            onError(it)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("MoodStore", "Failed to fetch batches", it)
                _isLoading.value = false
                onError(it)
            }
    }
}
