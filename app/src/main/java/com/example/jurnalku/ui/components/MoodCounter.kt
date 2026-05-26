package com.example.jurnalku.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.pss.StressResult
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Orange
import com.example.jurnalku.ui.theme.Red
import com.example.jurnalku.ui.theme.White

data class MoodData(
    val label: String,
    val count: Int,
    val color: Color,
    val emoji: String
)

@Composable
fun MoodCounter(
    moods: List<MoodData>,
    stressResult: StressResult? = null
) {
    val total = moods.sumOf { it.count }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mood Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Weekly summary",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            val safeStressResult = stressResult ?: StressResult()

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = safeStressResult.stressLevel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (safeStressResult.stressLevel) {
                        "Stress Rendah" -> Green
                        "Stress Sedang" -> Orange
                        "Stress Tinggi" -> Red
                        else -> Color.Gray
                    }
                )

                Text(
                    text = "Avg Mood: %.2f".format(safeStressResult.averageMood),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeWidth = 20.dp.toPx()
                val diameter = size.width * 0.75f
                val topLeft = Offset((size.width - diameter) / 2, size.height - (diameter / 2))
                val arcSize = Size(diameter, diameter)

                // Draw background gray arc
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )

                if (total > 0) {
                    var currentStartAngle = 180f
                    moods.forEach { mood ->
                        if (mood.count > 0) {
                            val sweepAngle = (mood.count.toFloat() / total) * 180f
                            drawArc(
                                color = mood.color,
                                startAngle = currentStartAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = topLeft,
                                size = arcSize
                            )
                            currentStartAngle += sweepAngle
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    text = total.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ENTRIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            moods.forEach { mood ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = mood.emoji)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mood.label.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (mood.count > 0) Color.Black else Color.Gray
                    )
                    Text(
                        text = mood.count.toString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (mood.count > 0) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}
