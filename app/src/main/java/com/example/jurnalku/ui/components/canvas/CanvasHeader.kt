package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun CanvasHeader(
    onClose: () -> Unit,
    onToggleDraw: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onExportJournal: () -> Unit,
    onSave: () -> Unit,
    onCreateNewPage: () -> Unit,
    onPickImage: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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

        // Right corner: Undo, Redo, and Options
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

            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    ComposableIcon(
                        icon = AppIconClass.More,
                        tint = JungleGreen,
                        size = 24.dp
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Export") },
                        onClick = {
                            showMenu = false
                            onExportJournal()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Save") },
                        onClick = {
                            showMenu = false
                            onSave()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("New Page") },
                        onClick = {
                            showMenu = false
                            onCreateNewPage()
                        }
                    )
                }
            }
        }
    }
}