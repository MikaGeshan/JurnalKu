package com.example.jurnalku.ui.journal.list

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
        uid: String,
        recentPage: RecentPageEntry,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val docId = "${uid}_${recentPage.journalId}_${recentPage.pageIndex}"
        
        // Serialize paths to JSON for storage
        val pathsJson = Gson().toJson(recentPage.paths)

        val data = mapOf(
            "uid" to uid,
            "journal_id" to recentPage.journalId,
            "journal_name" to recentPage.journalName,
            "page_index" to recentPage.pageIndex,
            "paper_type" to recentPage.paperType,
            "paper_color" to recentPage.paperColor,
            "text" to recentPage.text,
            "paths_json" to pathsJson,
            "image_base64" to recentPage.imageBase64,
            "image_offset_x" to recentPage.imageOffsetX,
            "image_offset_y" to recentPage.imageOffsetY,
            "image_scale" to recentPage.imageScale,
            "image_rotation" to recentPage.imageRotation,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("recent_pages")
            .document(docId)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getRecentPages(
        uid: String,
        onSuccess: (List<RecentPageEntry>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("recent_pages")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val recentPages = result.documents.mapNotNull { doc ->
                    try {
                        val pathsJson = doc.getString("paths_json")
                        val type = object : com.google.gson.reflect.TypeToken<List<DrawPathPayload>>() {}.type
                        val paths = if (pathsJson != null) {
                            Gson().fromJson<List<DrawPathPayload>>(pathsJson, type)
                        } else emptyList()

                        Log.d("GET_RECENT_PAGES", "Parsed page: ${doc.id}, paths: ${paths.size}, text: ${doc.getString("text")?.length ?: 0}")

                        RecentPageEntry(
                            journalId = doc.getString("journal_id") ?: "",
                            journalName = doc.getString("journal_name") ?: "",
                            pageIndex = doc.getLong("page_index")?.toInt() ?: 0,
                            paperType = doc.getString("paper_type") ?: "",
                            paperColor = doc.getLong("paper_color") ?: 0L,
                            text = doc.getString("text") ?: "",
                            paths = paths,
                            imageBase64 = doc.getString("image_base64"),
                            imageOffsetX = doc.getDouble("image_offset_x")?.toFloat() ?: 0f,
                            imageOffsetY = doc.getDouble("image_offset_y")?.toFloat() ?: 0f,
                            imageScale = doc.getDouble("image_scale")?.toFloat() ?: 1f,
                            imageRotation = doc.getDouble("image_rotation")?.toFloat() ?: 0f,
                            timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                        )
                    } catch (e: Exception) {
                        Log.e("GET_RECENT_PAGES", "Failed to parse page", e)
                        null
                    }
                }
                onSuccess(recentPages)
            }
            .addOnFailureListener { onError(it) }
    }
}
