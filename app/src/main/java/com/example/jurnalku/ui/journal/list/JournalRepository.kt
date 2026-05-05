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
        payload: JournalPayload,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val json = Gson().toJson(payload)

        val base64 = Base64.encodeToString(
            json.toByteArray(),
            Base64.DEFAULT
        )

        val docId = UUID.randomUUID().toString()

        val data = mapOf(
            "uid" to uid,
            "content_id" to payload.contentId,
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
        contentId: String,
        payload: JournalPayload,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = Gson().toJson(payload)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)

        db.collection("journals")
            .whereEqualTo("content_id", contentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val docId = querySnapshot.documents[0].id
                    db.collection("journals")
                        .document(docId)
                        .update("payload", base64)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    onError(Exception("Journal not found"))
                }
            }
            .addOnFailureListener { onError(it) }
    }

    fun getJournal(
        contentId: String,
        onSuccess: (JournalPayload) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("journals")
            .whereEqualTo("content_id", contentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val payloadBase64 = querySnapshot.documents[0].getString("payload")
                    if (payloadBase64 != null) {
                        try {
                            val decodedBytes = Base64.decode(payloadBase64, Base64.DEFAULT)
                            val json = String(decodedBytes)
                            val journal = Gson().fromJson(json, JournalPayload::class.java)
                            onSuccess(journal)
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
