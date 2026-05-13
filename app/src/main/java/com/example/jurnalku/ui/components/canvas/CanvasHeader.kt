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
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onSave: () -> Unit,
    onPickImage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left corner: Close icon
        Text(
            text = "X",
            modifier = Modifier
                .clickable { onClose() }
                .padding(8.dp),
            fontWeight = FontWeight.Bold
        )

        // Center: ImagePicker and Toggle Draw
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPickImage) {
                ComposableIcon(icon = AppIconClass.Journal, tint = Color.Unspecified, size = 24.dp)
            }

            IconButton(onClick = onToggleDraw) {
                ComposableIcon(icon = AppIconClass.PenTool, tint = Color.Unspecified, size = 24.dp)
            }
        }

        // Right corner: Undo, Redo, and Save
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onUndo,
                enabled = canUndo
            ) {
                ComposableIcon(
                    icon = AppIconClass.Undo, 
                    tint = if (canUndo) Color.Unspecified else Color.Gray, 
                    size = 24.dp
                )
            }

            IconButton(
                onClick = onRedo,
                enabled = canRedo
            ) {
                ComposableIcon(
                    icon = AppIconClass.Redo, 
                    tint = if (canRedo) Color.Unspecified else Color.Gray, 
                    size = 24.dp
                )
            }

            Text(
                text = "Save",
                modifier = Modifier
                    .clickable { onSave() }
                    .padding(start = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}