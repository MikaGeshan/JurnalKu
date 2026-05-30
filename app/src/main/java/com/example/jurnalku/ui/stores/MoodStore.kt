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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    private val _lastPSSScore = MutableStateFlow<Int?>(null)
    val lastPSSScore: StateFlow<Int?> = _lastPSSScore

    private val _lastPSSDate = MutableStateFlow<Long?>(null)
    val lastPSSDate: StateFlow<Long?> = _lastPSSDate

    private val _stressResult = MutableStateFlow<StressResult?>(null)
    val stressResult: StateFlow<StressResult?> = _stressResult

    private val _latestBatchSize = MutableStateFlow(0)
    val latestBatchSize: StateFlow<Int> = _latestBatchSize

    fun calculateWeeklyStats(date: Date = Date()) {
        val cal = Calendar.getInstance()
        cal.time = date

        // mulai dari 6 hari lalu
        cal.add(Calendar.DATE, -6)

        // normalize
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val weeklyMoods = mutableListOf<MoodClass>()

        repeat(7) {
            val dateStr = sdf.format(cal.time)

            _moodHistory.value[dateStr]?.let { key ->
                MoodClass.all.find { it.key == key }?.let {
                    weeklyMoods.add(it)
                }
            }

            cal.add(Calendar.DATE, 1)
        }

        val moodValues = weeklyMoods.map { it.value }

        val pssScore = _lastPSSScore.value ?: 0

        val result = StressCalculator.calculate(
            pssScore,
            moodValues
        )

        _weeklyMoodData.value = MoodClass.all.map { mood ->
            MoodData(
                label = mood.key,
                count = weeklyMoods.count { it.key == mood.key },
                color = mood.color,
                emoji = mood.emoji
            )
        }

        _stressResult.value = result
    }

    fun setPSSScore(uid: String, score: Int) {
        _lastPSSScore.value = score
        _lastPSSDate.value = System.currentTimeMillis()
        calculateWeeklyStats()
        savePSSScore(uid, score)
    }

    private fun savePSSScore(uid: String, score: Int) {
        val data = mapOf(
            "uid" to uid,
            "score" to score,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("pss_entries")
            .add(data)
            .addOnSuccessListener { Log.d("MoodStore", "PSS score saved") }
            .addOnFailureListener { Log.e("MoodStore", "Failed to save PSS score", it) }
    }

    private fun fetchLatestPSS(uid: String) {
        db.collection("pss_entries")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val doc = querySnapshot.documents.firstOrNull()
                if (doc != null) {
                    _lastPSSScore.value = doc.getLong("score")?.toInt()
                    _lastPSSDate.value = doc.getLong("timestamp")
                    calculateWeeklyStats()
                }
            }
            .addOnFailureListener { Log.e("MoodStore", "Failed to fetch latest PSS", it) }
    }

    private fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    fun fetchTodayMood(uid: String) {
        _isLoading.value = true
        Log.d("MoodStore", "Fetching mood history for UID: $uid")
        db.collection("mood_entries")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fullHistory = mutableMapOf<String, String>()
                Log.d("MoodStore", "Found ${querySnapshot.size()} batch documents")
                
                querySnapshot.documents.forEach { doc ->
                    doc.data?.forEach { (key, value) ->
                        if (key.startsWith("moods")) {
                            val moodsMap = value as? Map<*, *> ?: emptyMap<Any, Any>()
                            moodsMap.forEach { (date, moodVal) ->
                                val dateStr = date.toString()
                                val moodValue = (moodVal as? Number)?.toInt() ?: 0
                                val moodKey = MoodClass.all.find { it.value == moodValue }?.key
                                if (moodKey != null) {
                                    fullHistory[dateStr] = moodKey
                                }
                            }
                        }
                    }
                }

                val latestDoc = querySnapshot.documents
                    .sortedByDescending { it.getLong("created_at") ?: 0L }
                    .firstOrNull()
                var countInLatest = 0
                latestDoc?.data?.filterKeys { it.startsWith("moods") }?.values?.forEach {
                    countInLatest += (it as? Map<*, *>)?.size ?: 0
                }
                _latestBatchSize.value = countInLatest

                _moodHistory.value = fullHistory
                calculateWeeklyStats()
                fetchLatestPSS(uid)
                
                val today = getTodayDate()
                _selectedMood.value = MoodClass.all.find { it.key == fullHistory[today] }
                _isLoading.value = false
                Log.d("MoodStore", "Total history entries parsed: ${fullHistory.size}")
            }
            .addOnFailureListener {
                Log.e("MoodStore", "Failed to fetch history", it)
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

        db.collection("mood_entries")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val latestDoc = querySnapshot.documents
                    .sortedByDescending { it.getLong("created_at") ?: 0L }
                    .firstOrNull()

                val data = latestDoc?.data ?: emptyMap<String, Any>()
                
                // 1. Check if today already exists in any moods_n field
                var foundField: String? = null
                data.forEach { (key, value) ->
                    if (key.startsWith("moods")) {
                        val moodsMap = value as? Map<*, *> ?: emptyMap<Any, Any>()
                        if (moodsMap.containsKey(today)) {
                            foundField = key
                        }
                    }
                }

                if (foundField != null) {
                    val rawMap = data[foundField!!] as? Map<*, *> ?: emptyMap<Any, Any>()
                    val moodsMap = mutableMapOf<String, Int>()
                    rawMap.forEach { (k, v) ->
                        moodsMap[k.toString()] = (v as? Number)?.toInt() ?: 0
                    }
                    
                    moodsMap[today] = mood.value
                    latestDoc?.reference?.update(
                        foundField!!, moodsMap,
                        "last_updated", System.currentTimeMillis()
                    )?.addOnSuccessListener {
                        _selectedMood.value = mood
                        fetchTodayMood(uid) 
                        _isLoading.value = false
                        onSuccess()
                    }?.addOnFailureListener {
                        _isLoading.value = false
                        onError(it)
                    }
                    return@addOnSuccessListener
                }

                // 2. Count total moods in this doc
                var totalMoodsInDoc = 0
                data.filterKeys { it.startsWith("moods") }.values.forEach { 
                    totalMoodsInDoc += (it as? Map<*, *>)?.size ?: 0 
                }

                if (latestDoc != null && totalMoodsInDoc < 30) {
                    // Find or create correct moods_n field (7 entries each)
                    var targetField = "moods_0"
                    for (i in 1..5) {
                        val fieldName = "moods_$i"
                        val fieldData = data[fieldName] as? Map<*, *>
                        if (fieldData == null || fieldData.size < 7) {
                            targetField = fieldName
                            break
                        }
                    }
                    
                    val rawMap = data[targetField] as? Map<*, *> ?: emptyMap<Any, Any>()
                    val moodsMap = mutableMapOf<String, Int>()
                    rawMap.forEach { (k, v) ->
                        moodsMap[k.toString()] = (v as? Number)?.toInt() ?: 0
                    }

                    moodsMap[today] = mood.value
                    
                    latestDoc.reference.update(
                        targetField, moodsMap,
                        "last_updated", System.currentTimeMillis()
                    ).addOnSuccessListener {
                        _selectedMood.value = mood
                        fetchTodayMood(uid) 
                        _isLoading.value = false
                        onSuccess()
                    }.addOnFailureListener {
                        _isLoading.value = false
                        onError(it)
                    }
                } else {
                    // Create a new batch document (first 30-day block)
                    val newPayload = mapOf(
                        "uid" to uid,
                        "moods_0" to mapOf(today to mood.value),
                        "created_at" to today,
                        "last_updated" to today
                    )
                    db.collection("mood_entries").add(newPayload)
                        .addOnSuccessListener {
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
