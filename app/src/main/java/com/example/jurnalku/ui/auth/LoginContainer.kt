package com.example.jurnalku.ui.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember

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

    fun handleLogin() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val payload = LoginPayload(
                email = email,
                password = password
            )

            Log.d("LoginPayload", "payload = $payload")

            onLoginSuccess()
        } else {
            Log.d("LoginPayload", "email atau password masih kosong")
        }
    }

    fun handleGoogleLogin() {
        // nanti isi Google Sign-In di sini
    }

    LoginScreen(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = { handleLogin() },
        onGoogleLoginClick = { handleGoogleLogin() },
        onNavigateToRegister = onNavigateToRegister
    )
}