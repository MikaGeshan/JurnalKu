package com.example.jurnalku.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.White

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false
            ),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = JungleGreen,
            contentColor = White
        )
    ) {
        Text(text)
    }
}