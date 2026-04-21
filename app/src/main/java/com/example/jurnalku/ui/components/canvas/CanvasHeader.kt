package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon

@Composable
fun CanvasHeader(
    onClose: () -> Unit,
    onToggleDraw: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // on close
        Text(
            text = "X",
            modifier = Modifier.clickable { onClose() },
            fontWeight = FontWeight.Bold
        )

        Row {

            IconButton (onClick = { /* TODO image picker */ }) {
                ComposableIcon(icon = AppIconClass.Journal, tint = Color.Unspecified, size = 24.dp)
            }

            IconButton(onClick = onToggleDraw) {
                ComposableIcon(icon = AppIconClass.PenTool, tint = Color.Unspecified, size = 24.dp)
            }

            IconButton(onClick = onUndo) {
                ComposableIcon(icon = AppIconClass.Undo, tint = Color.Unspecified, size = 24.dp)
            }

            IconButton(onClick = onRedo) {
                ComposableIcon(icon = AppIconClass.Redo, tint = Color.Unspecified, size = 24.dp)
            }
        }
    }
}