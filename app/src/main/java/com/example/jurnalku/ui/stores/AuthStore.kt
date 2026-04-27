package com.example.jurnalku.ui.stores

import androidx.lifecycle.ViewModel
import com.example.jurnalku.ui.auth.UserClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthStore : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<UserClass?>(null)
    val user: StateFlow<UserClass?> = _user

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            _user.value = firebaseUser?.let { user ->
                UserClass(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email
                )
            }
        }
    }

    val isLoggedIn: Boolean
        get() = _user.value != null

    fun logout() {
        auth.signOut()
    }
}