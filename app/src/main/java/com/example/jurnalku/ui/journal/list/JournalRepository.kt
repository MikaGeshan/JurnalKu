package com.example.jurnalku.ui.journal.list

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.UUID

class JournalRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveJournal(
        uid: String,
        journalEntry: JournalEntry,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val json = Gson().toJson(journalEntry.pages)

        val base64 = Base64.encodeToString(
            json.toByteArray(),
            Base64.DEFAULT
        )

        val docId = journalEntry.journalId

        val data = mapOf(
            "uid" to uid,
            "journal_id" to journalEntry.journalId,
            "journal_name" to journalEntry.journalName,
            "pages" to base64,
            "created_at" to FieldValue.serverTimestamp()
        )

        db.collection("journals")
            .document(docId)
            .set(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun updateJournal(
        journalId: String,
        pages: List<JournalPagePayload>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = Gson().toJson(pages)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)

        db.collection("journals")
            .document(journalId)
            .update("pages", base64)
            .addOnSuccessListener { onSuccess() }
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
                            onSuccess(
                                JournalEntry(
                                    journalId = journalId,
                                    journalName = journalName,
                                    pages = pages
                                )
                            )
                        } catch (e: Exception) {
                            onError(e)
                        }
                    } else {
                        onError(Exception("Pages is null"))
                    }
                } else {
                    onError(Exception("Journal not found"))
                }
            }
            .addOnFailureListener { onError(it) }
    }

    fun saveRecentPage(
        context: Context,
        uid: String,
        recentPage: RecentPageEntry,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        try {
            val prefs = context.getSharedPreferences("recent_pages_prefs", Context.MODE_PRIVATE)
            val key = "recent_pages_$uid"
            
            val existingJson = prefs.getString(key, null)
            val type = object : com.google.gson.reflect.TypeToken<MutableList<RecentPageEntry>>() {}.type
            val recentList: MutableList<RecentPageEntry> = if (existingJson != null) {
                Gson().fromJson(existingJson, type)
            } else {
                mutableListOf()
            }

            // Remove if exists (same journal and page)
            recentList.removeAll { it.journalId == recentPage.journalId && it.pageIndex == recentPage.pageIndex }
            
            // Add to front
            recentList.add(0, recentPage.copy(timestamp = System.currentTimeMillis()))
            
            // Limit to 10
            val limitedList = recentList.take(10)
            
            val newJson = Gson().toJson(limitedList)
            prefs.edit().putString(key, newJson).apply()
            
            Log.d("JournalRepository", "Saved recent page locally for $uid")
            onSuccess()
        } catch (e: Exception) {
            Log.e("JournalRepository", "Failed to save recent page locally", e)
            onError(e)
        }
    }

    fun getRecentPages(
        context: Context,
        uid: String,
        onSuccess: (List<RecentPageEntry>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val prefs = context.getSharedPreferences("recent_pages_prefs", Context.MODE_PRIVATE)
            val key = "recent_pages_$uid"
            val json = prefs.getString(key, null)
            
            if (json != null) {
                val type = object : com.google.gson.reflect.TypeToken<List<RecentPageEntry>>() {}.type
                val pages = Gson().fromJson<List<RecentPageEntry>>(json, type)
                onSuccess(pages)
            } else {
                onSuccess(emptyList())
            }
        } catch (e: Exception) {
            Log.e("JournalRepository", "Failed to get recent pages locally", e)
            onError(e)
        }
    }
}
