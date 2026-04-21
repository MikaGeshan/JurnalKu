package com.example.jurnalku.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.Grey

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = { Text(label) },

        placeholder = {
            Text(
                text = label,
                color = Color.Black
            )
        },

        shape = RoundedCornerShape(16.dp),

        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp)), // 🔥 shadow

        singleLine = true,

        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Grey,
            unfocusedBorderColor = Grey,
            disabledBorderColor = Grey,

            focusedContainerColor = Grey,
            unfocusedContainerColor = Grey,
            disabledContainerColor = Grey,

            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,

            cursorColor = Color.Black
        )
    )
}