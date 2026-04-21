package com.example.jurnalku.ui.auth

import android.util.Log
import androidx.compose.runtime.*

data class RegisterPayload(
    val email: String,
    val password: String
)

@Composable
fun RegisterContainer(
    onRegisterSuccess: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun handleRegister() {
        if (email.isNotEmpty() && password.isNotEmpty()) {

            val payload = RegisterPayload(
                email = email,
                password = password
            )

            Log.d("RegisterPayload", "payload = $payload")

            onRegisterSuccess()
        } else {
            Log.d("RegisterPayload", "email atau password kosong")
        }
    }

    RegisterScreen(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onRegisterClick = { handleRegister() }
    )
}