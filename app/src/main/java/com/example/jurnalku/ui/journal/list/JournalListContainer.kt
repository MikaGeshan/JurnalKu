package com.example.jurnalku.ui.journal.list

import android.util.Log
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.stores.AuthStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

@Composable
fun JournalListContainer(
    navController: NavController
) {

    val authStore: AuthStore = viewModel()
    val user by authStore.user.collectAsState()
    val getUserUid = user?.uid ?: return

    var isLoading by remember { mutableStateOf(false) }

    val onNavigateEntries = {
        navController.navigate("entries")
    }

    val onNavigateCreateJournal = {
        navController.navigate("entries")
    }

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

    JournalListScreen(
        uid = getUserUid,
        isLoading = isLoading,
        onNavigateEntries = onNavigateEntries,
        onNavigateCreateJournal = onNavigateCreateJournal,
        getListJournal = ::getListJournal
    )
}