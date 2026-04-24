package com.example.jurnalku.ui.stores

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthStore : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow(auth.currentUser)
    val user: StateFlow<com.google.firebase.auth.FirebaseUser?> = _user

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun refreshUser() {
        _user.value = auth.currentUser
    }

    fun logout() {
        auth.signOut()
        _user.value = null
    }
}