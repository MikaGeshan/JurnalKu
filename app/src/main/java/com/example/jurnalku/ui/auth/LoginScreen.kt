package com.example.jurnalku.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.CustomButton
import com.example.jurnalku.ui.components.CustomLoadingSpinner
import com.example.jurnalku.ui.components.TextInput
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun LoginScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "JurnalKu",
                style = MaterialTheme.typography.headlineLarge,
                color = JungleGreen,
            )

            Spacer(modifier = Modifier.height(100.dp))

            TextInput(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                isError = email.isBlank() && errorMessage !== null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            TextInput(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                isPassword = true,
                isError = password.isBlank() && errorMessage !== null,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    color = androidx.compose.ui.graphics.Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            CustomButton(
                text = "LOGIN",
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "Or login with",
                style = MaterialTheme.typography.bodyLarge,
                color = JungleGreen
            )

            Spacer(modifier = Modifier.height(34.dp))

            CustomButton(
                text = "Google",
                onClick = onGoogleLoginClick
            )

            Spacer(modifier = Modifier.height(55.dp))

            Row {
                Text("Not Registered? ")

                Text(
                    text = "Sign Up Now",
                    color = JungleGreen,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        onNavigateToRegister()
                    }
                )
            }
        }

        if (isLoading) {
            CustomLoadingSpinner(isOverlay = true)
        }
    }
}