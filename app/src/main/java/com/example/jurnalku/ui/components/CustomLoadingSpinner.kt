package com.example.jurnalku.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun CustomLoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color = JungleGreen
) {

    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Canvas(
        modifier = modifier.size(40.dp)
    ) {

        val strokeWidth = 6.dp.toPx()
        val radius = size.minDimension / 2

        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            ),
            topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}