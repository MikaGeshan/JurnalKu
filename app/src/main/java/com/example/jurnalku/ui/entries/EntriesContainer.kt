package com.example.jurnalku.ui.entries

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.journal.list.JournalPayload
import com.example.jurnalku.ui.stores.AuthStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

@Composable
fun EntriesContainer(
    navController: NavController,
    onNavigateToLogin: () -> Unit
) {

    val authStore : AuthStore = viewModel()
    val user by authStore.user.collectAsState()
    val getUserName = user?.name?.split(" ")?.firstOrNull() ?: "User"
    val getUserUid = user?.uid ?: return

    var isLoading by remember { mutableStateOf(false) }

    val onNavigateCreateJournal = {
        navController.navigate("create_journal")
    }

    var selectedMood by remember { mutableStateOf<MoodClass?>(null) }

    fun handleMoodSelect(mood: MoodClass) {
        selectedMood = mood

        val payload = mapOf(
            "mood" to mood.key
        )

        Log.d("=== mood payload", payload.toString())
    }

//    fun handleDeleteJournal

    fun getListJournal(
        uid: String,
        onSuccess: (List<JournalPayload>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        isLoading = true

        FirebaseFirestore.getInstance()
            .collection("journals")
            .whereEqualTo("uid", uid)
            .get()

            .addOnSuccessListener { result ->

                val journals = result.documents.mapNotNull { document ->

                    try {

                        val payloadBase64 =
                            document.getString("payload")
                                ?: return@mapNotNull null

                        val decodedBytes = Base64.decode(
                            payloadBase64,
                            Base64.DEFAULT
                        )

                        val json = String(decodedBytes)

                        Gson().fromJson(
                            json,
                            JournalPayload::class.java
                        )

                    } catch (e: Exception) {
                        null
                    }
                }

                isLoading = false

                Log.d(
                    "FIRESTORE",
                    journals.toString()
                )

                onSuccess(journals)
            }

            .addOnFailureListener {

                isLoading = false

                onError(it)
            }
    }

    fun handleDeleteJournal(
        contentId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        isLoading = true

        FirebaseFirestore.getInstance()
            .collection("journals")
            .whereEqualTo("content_id", contentId)
            .get()
            .addOnSuccessListener { result ->
                val batch = FirebaseFirestore.getInstance().batch()
                result.documents.forEach { batch.delete(it.reference) }
                batch.commit()
                    .addOnSuccessListener {
                        isLoading = false
                        onSuccess()
                    }
                    .addOnFailureListener {
                        isLoading = false
                        onError(it)
                    }
            }
            .addOnFailureListener {
                isLoading = false
                onError(it)
            }
    }

    fun handleLogout() {
        Log.d("LOG_OUT", "logout")

        authStore.logout()

        onNavigateToLogin()
    }

    EntriesScreen(
        uid = getUserUid,
        name = getUserName,
        isLoading = isLoading,
        selectedMood = selectedMood,
        onMoodSelected = ::handleMoodSelect,
        onNavigateCreateJournal = onNavigateCreateJournal,
        getListJournal = ::getListJournal,
        onDeleteJournal = ::handleDeleteJournal,
        onLogOut = ::handleLogout
    )
}