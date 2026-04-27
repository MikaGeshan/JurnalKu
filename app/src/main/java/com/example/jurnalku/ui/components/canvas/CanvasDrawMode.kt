package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun CanvasDrawMode(
    paths: MutableList<DrawPath>,
    enabled: Boolean,
    color: Color,
    strokeWidth: Float
) {
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    var drawColor by remember { mutableStateOf(color) }
    var drawStroke by remember { mutableStateOf(strokeWidth) }

    val drawModifier = if (enabled) {
        Modifier
            .fillMaxSize()
            .pointerInput(color, strokeWidth) {
                detectDragGestures(
                    onDragStart = {
                        currentPath = listOf(it)
                        drawColor = color
                        drawStroke = strokeWidth
                    },
                    onDrag = { change, _ ->
                        currentPath = currentPath + change.position
                    },
                    onDragEnd = {
                        paths.add(
                            DrawPath(
                                points = currentPath,
                                color = drawColor,
                                strokeWidth = drawStroke
                            )
                        )
                        currentPath = emptyList()
                    }
                )
            }
    } else {
        Modifier.fillMaxSize()
    }

    Canvas(
        modifier = drawModifier
    ) {

        // saved paths
        paths.forEach { path ->
            drawPath(
                path = Path().apply {
                    path.points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y)
                        else lineTo(p.x, p.y)
                    }
                },
                color = path.color,
                style = Stroke(width = path.strokeWidth)
            )
        }

        // preview path
        if (currentPath.isNotEmpty()) {
            drawPath(
                path = Path().apply {
                    currentPath.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y)
                        else lineTo(p.x, p.y)
                    }
                },
                color = drawColor,
                style = Stroke(width = drawStroke)
            )
        }
    }
}