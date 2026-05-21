package com.example.jurnalku.ui.stores

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.jurnalku.ui.entries.MoodClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class MoodStore(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val prefs = application.getSharedPreferences("mood_cache", Context.MODE_PRIVATE)

    private val _selectedMood = MutableStateFlow<MoodClass?>(null)
    val selectedMood: StateFlow<MoodClass?> = _selectedMood

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun getWeekId(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "${year}_W${week}"
    }

    fun fetchTodayMood(uid: String) {
        val today = getTodayDate()
        val cachedMoodKey = prefs.getString(today, null)

        if (cachedMoodKey != null) {
            _selectedMood.value = MoodClass.all.find { it.key == cachedMoodKey }
        } else {
            // If not in cache, optionally check Firestore for today specifically 
            // but since we sync weekly, we usually rely on cache for the current week.
            val docId = "${uid}_${today}"
            db.collection("mood_entries")
                .document(docId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val moodKey = document.getString("mood_key")
                        _selectedMood.value = MoodClass.all.find { it.key == moodKey }
                        // Update cache
                        moodKey?.let { prefs.edit().putString(today, it).apply() }
                    }
                }
        }
    }

    fun saveMood(
        uid: String,
        mood: MoodClass,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val today = getTodayDate()
        
        // 1. Save to Local Cache immediately
        prefs.edit().putString(today, mood.key).apply()
        _selectedMood.value = mood

        // 2. Logic to decide when to upload (e.g., Every day to keep it safe, but in one doc)
        // Even if we update daily, using the WeekID as the Document ID keeps the DB "Light"
        syncWeeklyMood(uid, onSuccess, onError)
    }

    private fun syncWeeklyMood(
        uid: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val weekId = getWeekId()
        val docId = "${uid}_$weekId"
        
        // Collect all moods for the current week from cache
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val weeklyMoodMap = mutableMapOf<String, Int>()
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        repeat(7) {
            val dateStr = sdf.format(cal.time)
            val moodKey = prefs.getString(dateStr, null)
            val moodValue = MoodClass.all.find { it.key == moodKey }?.value
            if (moodValue != null) {
                weeklyMoodMap[dateStr] = moodValue
            }
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }

        if (weeklyMoodMap.isEmpty()) return

        val payload = mapOf(
            "uid" to uid,
            "week_id" to weekId,
            "moods" to weeklyMoodMap,
            "last_updated" to System.currentTimeMillis()
        )

        _isLoading.value = true
        db.collection("weekly_moods")
            .document(docId)
            .set(payload)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _isLoading.value = false
                onError(it)
            }
    }
}
