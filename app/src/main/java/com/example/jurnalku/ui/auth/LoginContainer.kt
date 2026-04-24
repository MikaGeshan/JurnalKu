package com.example.jurnalku.ui.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.stores.AuthStore
import com.google.firebase.auth.FirebaseAuth

data class LoginPayload(
    val email: String,
    val password: String
)

@Composable
fun LoginContainer(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = remember { FirebaseAuth.getInstance() }
    val authStore: AuthStore = viewModel()

    fun handleLogin() {
        if (isLoading) return

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email & password wajib diisi"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("LOGIN_SUCCESS", "uid=${user?.uid}")

                    authStore.refreshUser()
                    onLoginSuccess()
                } else {
                    errorMessage = task.exception?.message ?: "Login gagal"
                    Log.e("LOGIN_ERROR", errorMessage!!)
                }
            }
    }

    fun handleGoogleLogin() {
        // next step
    }

    LoginScreen(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = { handleLogin() },
        onGoogleLoginClick = { handleGoogleLogin() },
        onNavigateToRegister = onNavigateToRegister,
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}