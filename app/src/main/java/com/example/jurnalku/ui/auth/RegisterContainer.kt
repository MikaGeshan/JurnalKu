package com.example.jurnalku.ui.auth

import android.util.Log
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RegisterContainer(
    onRegisterSuccess: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = remember { FirebaseAuth.getInstance() }

    fun handleRegister() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email & password wajib diisi"
            return
        }

        if (password.length < 6) {
            errorMessage = "Password minimal 6 karakter"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("REGISTER_SUCCESS", "uid=${user?.uid}")

                    onRegisterSuccess()
                } else {
                    errorMessage = task.exception?.message ?: "Register gagal"
                    Log.e("REGISTER_ERROR", errorMessage!!)
                }
            }
    }

    RegisterScreen(
        email = email,
        password = password,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onRegisterClick = { handleRegister() }
    )
}