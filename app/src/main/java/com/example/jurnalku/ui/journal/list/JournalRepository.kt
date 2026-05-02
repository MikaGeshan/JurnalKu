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
}