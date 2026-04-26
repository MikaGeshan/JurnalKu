package com.example.jurnalku.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun PaperTypePreview(
    type: String,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = JungleGreen,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            when (type) {

                "Dot Grid" -> {
                    val spacing = 20f
                    for (x in 0..size.width.toInt() step spacing.toInt()) {
                        for (y in 0..size.height.toInt() step spacing.toInt()) {
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.5f),
                                radius = 2f,
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        }
                    }
                }

                "Grid" -> {
                    val spacing = 40f

                    for (x in 0..size.width.toInt() step spacing.toInt()) {
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), size.height)
                        )
                    }

                    for (y in 0..size.height.toInt() step spacing.toInt()) {
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat())
                        )
                    }
                }

                "Lined" -> {
                    val spacing = 40f

                    for (y in 0..size.height.toInt() step spacing.toInt()) {
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat())
                        )
                    }
                }
// Blank
            }
        }
    }
}