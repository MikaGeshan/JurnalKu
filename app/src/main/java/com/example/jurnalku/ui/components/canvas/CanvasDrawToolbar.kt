package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.Blue
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.Orange
import com.example.jurnalku.ui.theme.Purple
import com.example.jurnalku.ui.theme.Red
import com.example.jurnalku.ui.theme.Yellow

enum class DrawTool {
    PEN, PENCIL, HIGHLIGHTER, ERASER
}

@Composable
fun DrawToolbar(
    selectedTool: DrawTool,
    selectedColor: Color,
    onToolSelected: (DrawTool) -> Unit,
    onColorSelected: (Color) -> Unit
) {

    val colors = listOf(
        Black,
        Red,
        Orange,
        Yellow,
        Green,
        Blue,
        Purple
    )

    val defaultColor = colors.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(12.dp)
    ) {

        // tools
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DrawTool.values().forEach { tool ->

                val isSelected = tool == selectedTool

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) JungleGreen else Color.White,
                    modifier = Modifier
                        .clickable { onToolSelected(tool) }
                ) {
                    Text(
                        text = tool.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // pallete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            colors.forEach { color ->

                val isSelected = color == selectedColor

                Box(
                    modifier = Modifier
                        .size(if (isSelected) 40.dp else 30.dp)
                        .padding(if (isSelected) 2.dp else 0.dp)
                        .background(color, shape = CircleShape)
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}