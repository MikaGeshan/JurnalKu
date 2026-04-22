import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.White

data class MoodData(
    val label: String,
    val count: Int,
    val color: Color
)

@Composable
fun MoodCounter(
    moods: List<MoodData>
) {

    val total = moods.sumOf { it.count }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(16.dp)
    ) {

        Text("Mood Count")
        Text("Tap on mood to see more")

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val strokeWidth = 24.dp.toPx()

                val diameter = size.width * 0.9f
                val arcRect = Rect(
                    left = (size.width - diameter) / 2,
                    top = 0f,
                    right = (size.width + diameter) / 2,
                    bottom = diameter
                )

                var startAngle = 180f

                moods.forEach { mood ->

                    val sweepAngle = if (total == 0) 0f
                    else (mood.count.toFloat() / total) * 180f

                    drawArc(
                        color = mood.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        topLeft = arcRect.topLeft,
                        size = arcRect.size
                    )

                    startAngle += sweepAngle
                }
            }

            Text(
                text = total.toString(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val moodLegend = listOf(
                "😄" to "Very Happy",
                "🙂" to "Happy",
                "😐" to "Sad",
                "😟" to "Very Sad",
                "😫" to "Awful"
            )

            moodLegend.forEach { (emoji, label) ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = emoji)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}