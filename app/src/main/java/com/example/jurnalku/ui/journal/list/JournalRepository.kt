package com.example.jurnalku.ui.journal.list

import android.util.Base64
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

        val json = Gson().toJson(journalEntry.payload)

        val base64 = Base64.encodeToString(
            json.toByteArray(),
            Base64.DEFAULT
        )

        val docId = journalEntry.journalId

        val data = mapOf(
            "uid" to uid,
            "journal_id" to journalEntry.journalId,
            "journal_name" to journalEntry.journalName,
            "payload" to base64,
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
        payload: JournalPagePayload,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = Gson().toJson(payload)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)

        db.collection("journals")
            .document(journalId)
            .update("payload", base64)
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
                    val payloadBase64 = document.getString("payload")
                    val journalName = document.getString("journal_name") ?: ""
                    if (payloadBase64 != null) {
                        try {
                            val decodedBytes = Base64.decode(payloadBase64, Base64.DEFAULT)
                            val json = String(decodedBytes)
                            val payload = Gson().fromJson(json, JournalPagePayload::class.java)
                            onSuccess(
                                JournalEntry(
                                    journalId = journalId,
                                    journalName = journalName,
                                    payload = payload
                                )
                            )
                        } catch (e: Exception) {
                            onError(e)
                        }
                    } else {
                        onError(Exception("Payload is null"))
                    }
                } else {
                    onError(Exception("Journal not found"))
                }
            }
            .addOnFailureListener { onError(it) }
    }
}
