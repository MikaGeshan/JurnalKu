import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CanvasPattern(type: String) {

    Canvas(modifier = Modifier.fillMaxSize()) {

        val lineColor = Color.Gray.copy(alpha = 0.3f)

        when (type) {

            "Lined" -> {
                val spacing = 24.dp.toPx()
                var y = spacing

                // garis horizontal
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2f
                    )
                    y += spacing
                }

                // margin kiri
                drawLine(
                    color = Color.Red.copy(alpha = 0.3f),
                    start = Offset(80f, 0f),
                    end = Offset(80f, size.height),
                    strokeWidth = 2f
                )
            }

            "Grid" -> {
                val spacing = 24.dp.toPx()

                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += spacing
                }

                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += spacing
                }
            }

            "Dot Grid" -> {
                val spacing = 16.dp.toPx()

                var y = spacing / 2
                while (y < size.height) {
                    var x = spacing / 2
                    while (x < size.width) {
                        drawCircle(
                            color = lineColor,
                            radius = 2f,
                            center = Offset(x, y)
                        )
                        x += spacing
                    }
                    y += spacing
                }
            }

            "Blank" -> Unit
        }
    }
}