package com.example.jurnalku.ui.entries

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.JournalEntry
import com.example.jurnalku.ui.journal.list.RecentPageEntry
import com.example.jurnalku.ui.journal.list.JournalRepository
import com.example.jurnalku.ui.stores.AuthStore
import com.example.jurnalku.ui.stores.MoodStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

@Composable
fun EntriesContainer(
    navController: NavController,
    onNavigateToLogin: () -> Unit
) {

    val authStore : AuthStore = viewModel()
    val moodStore : MoodStore = viewModel()
    val user by authStore.user.collectAsState()
    val getUserName = user?.name?.split(" ")?.firstOrNull() ?: "User"
    val getUserUid = user?.uid ?: return
    val context = androidx.compose.ui.platform.LocalContext.current

    val selectedMood by moodStore.selectedMood.collectAsState()
    val todayMoodCount by moodStore.todayMoodCount.collectAsState()
    val isMoodLoading by moodStore.isLoading.collectAsState()

    val repository = remember { JournalRepository() }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(getUserUid) {
        moodStore.fetchTodayMood(getUserUid)
    }

    val onNavigateCreateJournal = {
        navController.navigate("create_journal")
    }

    fun handleMoodSelect(mood: MoodClass) {
        if (todayMoodCount < 3) {
            moodStore.saveMood(getUserUid, mood)
        }
    }


    fun getRecentPages(
        uid: String,
        onSuccess: (List<RecentPageEntry>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        repository.getRecentPages(context, uid, onSuccess, onError)
    }

    fun getListJournal(
        uid: String,
        onSuccess: (List<JournalEntry>) -> Unit,
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

                        val pagesBase64 =
                            document.getString("pages")
                                ?: return@mapNotNull null

                        val journalId = document.id
                        val journalName = document.getString("journal_name") ?: ""

                        val decodedBytes = Base64.decode(
                            pagesBase64,
                            Base64.DEFAULT
                        )

                        val json = String(decodedBytes)
                        val type = object : com.google.gson.reflect.TypeToken<List<JournalPagePayload>>() {}.type
                        val pages = Gson().fromJson<List<JournalPagePayload>>(
                            json,
                            type
                        )

                        JournalEntry(
                            journalId = journalId,
                            journalName = journalName,
                            pages = pages
                        )

                    } catch (e: Exception) {
                        null
                    }
                }

                isLoading = false
                onSuccess(journals)
            }

            .addOnFailureListener {

                isLoading = false

                onError(it)
            }
    }

    fun handleDeleteJournal(
        journalId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        isLoading = true

        FirebaseFirestore.getInstance()
            .collection("journals")
            .document(journalId)
            .delete()
            .addOnSuccessListener {
                isLoading = false
                onSuccess()
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

    val onEditJournal = { journal: JournalEntry ->
        navController.navigate("edit_journal/${journal.journalId}")
    }

    EntriesScreen(
        uid = getUserUid,
        name = getUserName,
        isLoading = isLoading || isMoodLoading,
        selectedMood = selectedMood,
        todayMoodCount = todayMoodCount,
        onMoodSelected = ::handleMoodSelect,
        onNavigateCreateJournal = onNavigateCreateJournal,
        getListJournal = ::getListJournal,
        getRecentPages = ::getRecentPages,
        onDeleteJournal = ::handleDeleteJournal,
        onEditJournal = onEditJournal,
        onLogOut = ::handleLogout
    )
}
