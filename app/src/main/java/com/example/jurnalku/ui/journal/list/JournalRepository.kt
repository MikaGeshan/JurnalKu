package com.example.jurnalku.ui.journal.list

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class JournalRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun logActivity(uid: String, type: String, journalName: String) {
        val today = getTodayDate()
        val docId = "${uid}_$today"
        
        val activity = mapOf(
            "type" to type,
            "name" to journalName,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("activity_log")
            .document(docId)
            .set(
                mapOf(
                    "uid" to uid,
                    "date" to today,
                    "activities" to FieldValue.arrayUnion(activity)
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
    }

    fun saveJournal(
        uid: String,
        journalEntry: JournalEntry,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = Gson().toJson(journalEntry.pages)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)
        val docId = journalEntry.journalId

        val data = mapOf(
            "uid" to uid,
            "journal_id" to journalEntry.journalId,
            "journal_name" to journalEntry.journalName,
            "pages" to base64,
            "created_at" to FieldValue.serverTimestamp(),
            "activity_dates" to FieldValue.arrayUnion(getTodayDate())
        )

        db.collection("journals")
            .document(docId)
            .set(data)
            .addOnSuccessListener {
                logActivity(uid, "JOURNAL_CREATED", journalEntry.journalName)
                onSuccess()
            }
            .addOnFailureListener { onError(it) }
    }

    fun updateJournal(
        uid: String, // Added uid for logging
        journalId: String,
        journalName: String, // Added for logging
        pages: List<JournalPagePayload>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = Gson().toJson(pages)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)

        db.collection("journals")
            .document(journalId)
            .update(
                "pages", base64,
                "activity_dates", FieldValue.arrayUnion(getTodayDate())
            )
            .addOnSuccessListener { 
                logActivity(uid, "JOURNAL_UPDATED", journalName)
                onSuccess() 
            }
            .addOnFailureListener { onError(it) }
    }

    fun deleteJournal(
        uid: String,
        journalId: String,
        journalName: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("journals")
            .document(journalId)
            .delete()
            .addOnSuccessListener {
                logActivity(uid, "JOURNAL_DELETED", journalName)
                onSuccess()
            }
            .addOnFailureListener { onError(it) }
    }

    fun getJournal(
        journalId: String,
        onSuccess: (JournalEntry) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("journals")
            .document(journalId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val pagesBase64 = document.getString("pages")
                    val journalName = document.getString("journal_name") ?: ""
                    if (pagesBase64 != null) {
                        try {
                            val decodedBytes = Base64.decode(pagesBase64, Base64.DEFAULT)
                            val json = String(decodedBytes)
                            val type = object : com.google.gson.reflect.TypeToken<List<JournalPagePayload>>() {}.type
                            val pages = Gson().fromJson<List<JournalPagePayload>>(json, type)
                            onSuccess(JournalEntry(journalId = journalId, journalName = journalName, pages = pages))
                        } catch (e: Exception) { onError(e) }
                    } else { onError(Exception("Pages is null")) }
                } else { onError(Exception("Journal not found")) }
            }
            .addOnFailureListener { onError(it) }
    }

    fun saveRecentPage(context: Context, uid: String, recentPage: RecentPageEntry) {
        try {
            val prefs = context.getSharedPreferences("recent_pages_prefs", Context.MODE_PRIVATE)
            val key = "recent_pages_$uid"
            val existingJson = prefs.getString(key, null)
            val type = object : com.google.gson.reflect.TypeToken<MutableList<RecentPageEntry>>() {}.type
            val recentList: MutableList<RecentPageEntry> = if (existingJson != null) Gson().fromJson(existingJson, type) else mutableListOf()

            recentList.removeAll { it.journalId == recentPage.journalId && it.pageIndex == recentPage.pageIndex }
            recentList.add(0, recentPage.copy(timestamp = System.currentTimeMillis()))
            val limitedList = recentList.take(10)
            prefs.edit().putString(key, Gson().toJson(limitedList)).apply()
        } catch (e: Exception) { Log.e("JournalRepository", "Failed to save recent page", e) }
    }

    fun getRecentPages(context: Context, uid: String, onSuccess: (List<RecentPageEntry>) -> Unit, onError: (Exception) -> Unit) {
        try {
            val prefs = context.getSharedPreferences("recent_pages_prefs", Context.MODE_PRIVATE)
            val key = "recent_pages_$uid"
            val json = prefs.getString(key, null)
            if (json != null) {
                val type = object : com.google.gson.reflect.TypeToken<List<RecentPageEntry>>() {}.type
                onSuccess(Gson().fromJson(json, type))
            } else { onSuccess(emptySet<RecentPageEntry>().toList()) }
        } catch (e: Exception) { onError(e) }
    }
}
