package com.example.jurnalku.ui.stores

import androidx.lifecycle.ViewModel
import com.example.jurnalku.ui.entries.MoodClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class MoodStore : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _selectedMood = MutableStateFlow<MoodClass?>(null)
    val selectedMood: StateFlow<MoodClass?> = _selectedMood

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchTodayMood(uid: String) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val docId = "${uid}_${date}"

        db.collection("mood_entries")
            .document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val moodKey = document.getString("mood_key")
                    _selectedMood.value = MoodClass.all.find { it.key == moodKey }
                }
            }
    }

    fun saveMood(
        uid: String,
        mood: MoodClass,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        _isLoading.value = true
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val docId = "${uid}_${date}"

        val payload = mapOf(
            "uid" to uid,
            "mood_key" to mood.key,
            "mood_value" to mood.value,
            "date" to date,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("mood_entries")
            .document(docId)
            .set(payload)
            .addOnSuccessListener {
                _selectedMood.value = mood
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _isLoading.value = false
                onError(it)
            }
    }
}
