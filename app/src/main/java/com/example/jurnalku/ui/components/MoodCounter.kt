import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
            .background(
                color = White,
            )
            .padding(16.dp)
    ) {

        Text("Mood Count")
        Text("Tap on mood to see more")

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {

            val strokeWidth = 24.dp.toPx()

            val diameter = size.width
            val arcRect = Rect(
                left = 0f,
                top = 0f,
                right = diameter,
                bottom = diameter
            )

            var startAngle = 180f

            moods.forEach { mood ->

                val sweepAngle = if (total == 0) {
                    0f
                } else {
                    (mood.count.toFloat() / total) * 180f
                }

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
    }
}