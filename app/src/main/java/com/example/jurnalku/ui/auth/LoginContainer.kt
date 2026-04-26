package com.example.jurnalku.ui.auth

import com.example.jurnalku.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jurnalku.ui.stores.AuthStore
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun LoginContainer(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

//  Check existing user data
    val auth = remember { FirebaseAuth.getInstance() }
    val authStore: AuthStore = viewModel()

//  handle function login
    fun handleLogin() {
        if (isLoading) return

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email & Password are required"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false

//              success & error validations
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

    val context = LocalContext.current

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken == null) {
                    Log.e("GOOGLE_LOGIN", "ID Token NULL")
                    errorMessage = "Google login gagal (config error)"
                    return@rememberLauncherForActivityResult
                }

                val credential = GoogleAuthProvider.getCredential(idToken, null)

                isLoading = true

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->

                        isLoading = false

                        if (task.isSuccessful) {
                            authStore.refreshUser()
                            onLoginSuccess()
                        } else {
                            errorMessage = "Google login gagal"
                        }
                    }

            } catch (e: Exception) {
                errorMessage = task.exception?.message ?: "Google login gagal"
                Log.e("GOOGLE_LOGIN_ERROR", errorMessage!!)
            }
        }
    }


//    handle login with google
    fun handleGoogleLogin() {
        if (isLoading) return

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
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