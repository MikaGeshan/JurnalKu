package com.example.jurnalku.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.CustomButton
import com.example.jurnalku.ui.components.CustomLoadingSpinner
import com.example.jurnalku.ui.components.TextInput
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.White

@Composable
fun RegisterScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
) {
    var step by remember { mutableIntStateOf(1) }

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
            color = JungleGreen
        )

        Spacer(modifier = Modifier.height(80.dp))

        when (step) {

            1 -> {
                TextInput(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "NEXT",
                    onClick = {
                        if (email.isNotBlank()) {
                            step = 2
                        }
                    }
                )
            }

            2 -> {
                TextInput(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "REGISTER",
                    onClick = {
                        if (!isLoading) onRegisterClick()
                    }
                ) {
                    if (isLoading) {
                        CustomLoadingSpinner(
                            modifier = Modifier.size(20.dp),
                            color = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Back",
                    color = JungleGreen,
                    modifier = Modifier
                        .clickable { step = 1 }
                )
            }
        }
    }
}