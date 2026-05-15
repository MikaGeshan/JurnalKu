package com.example.jurnalku.ui.components

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jurnalku.ui.journal.list.RecentPageEntry
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun PagePreview(
    recentPage: RecentPageEntry,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(recentPage) {
        Log.d("PAGE_PREVIEW", "Rendering page for ${recentPage.journalName}, paths: ${recentPage.paths.size}, text: ${recentPage.text.length}")
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(recentPage.paperColor.toULong()), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 0.dp,
                color = JungleGreen,
                shape = RoundedCornerShape(12.dp)
            )
            .clipToBounds()
    ) {
        // 1. Paper Pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (recentPage.paperType) {
                "Dot Grid" -> {
                    for (x in 0..size.width.toInt() step 60) {
                        for (y in 0..size.height.toInt() step 60) {
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.3f),
                                radius = 1f,
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        }
                    }
                }
                "Grid" -> {
                    val gridSpacing = 40f
                    for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                        drawLine(Color.Gray.copy(alpha = 0.2f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height))
                    }
                    for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                        drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
                    }
                }
                "Lined" -> {
                    val lineSpacing = 40f
                    for (y in 0..size.height.toInt() step lineSpacing.toInt()) {
                        drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
                    }
                }
            }
        }

        // 2. Image Layer (Compressed for preview)
        recentPage.imageBase64?.let { base64String ->
            val imageBytes = remember(base64String) {
                try {
                    Base64.decode(base64String, Base64.DEFAULT)
                } catch (_: Exception) {
                    null
                }
            }
            if (imageBytes != null) {
                AsyncImage(
                    model = imageBytes,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Scale down original transforms for preview
                            // Since the box is roughly 1/3 of the screen, we scale offsets
                            translationX = recentPage.imageOffsetX * 0.3f
                            translationY = recentPage.imageOffsetY * 0.3f
                            scaleX = recentPage.imageScale
                            scaleY = recentPage.imageScale
                            rotationZ = recentPage.imageRotation
                        },
                    contentScale = ContentScale.Fit,
                    alpha = 0.8f // Slightly fade for preview style
                )
            }
        }

        // 3. Text Layer
        if (recentPage.text.isNotBlank()) {
            Text(
                text = recentPage.text,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 6.sp, // Very small for preview
                    lineHeight = 8.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                ),
                maxLines = 15
            )
        }

        // 4. Drawing Layer (Simplified)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // We scale all paths to fit the preview box
            // Assuming reference width of 1080 (standard full HD width)
            val scale = size.width / 1080f
            
            recentPage.paths.forEach { pathPayload ->
                if (pathPayload.points.size > 1) {
                    val p = Path()
                    val firstPoint = pathPayload.points.first()
                    p.moveTo(firstPoint.x * scale, firstPoint.y * scale)
                    
                    // PERFORMANCE: Skip points for preview if too many
                    val step = if (pathPayload.points.size > 50) 2 else 1
                    for (i in 1 until pathPayload.points.size step step) {
                        val point = pathPayload.points[i]
                        p.lineTo(point.x * scale, point.y * scale)
                    }
                    
                    drawPath(
                        path = p,
                        color = Color(pathPayload.color.toULong()).copy(alpha = 0.8f),
                        style = Stroke(
                            width = pathPayload.strokeWidth * scale,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
