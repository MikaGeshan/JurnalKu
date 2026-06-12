package com.example.jurnalku.ui.components.pss

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import java.util.Calendar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.Grey

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Orange
import com.example.jurnalku.ui.theme.Red

import androidx.compose.material3.MaterialTheme

@Composable
fun ResultStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Black)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun ScoreReferenceRow(range: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = range, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(70.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun PSSForm(
    lastTakenDate: Long? = null,
    savedScore: Int? = null,
    moodValues: List<Int>? = null,
    onScoreCalculated: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val schema = remember { loadPSS(context) }

    var currentIndex by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val canTakeTest = remember(lastTakenDate) {
        if (lastTakenDate == null) {
            true
        } else {
            val lastCalendar = Calendar.getInstance().apply {
                timeInMillis = lastTakenDate
                add(Calendar.DAY_OF_YEAR, 30)
            }
            val now = Calendar.getInstance()
            now.after(lastCalendar) || now.timeInMillis >= lastCalendar.timeInMillis
        }
    }

    val answers = remember {
        mutableStateListOf<Int?>().apply {
            repeat(schema.questions.size) { add(null) }
        }
    }

    val showResultPss = isFinished || !canTakeTest

    if (showResultPss) {
        val pssScore = if (isFinished) {
            calculatePSSScore(schema, answers)
        } else {
            savedScore ?: 0
        }

        val result = StressCalculator.calculate(pssScore, moodValues)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stress Analysis Result",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultStatItem("Avg Mood", "%.1f".format(result.averageMood))
                ResultStatItem("PSS Score", result.pss4Score.toString())
                ResultStatItem("Final Score", "%.1f".format(result.finalScore))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Final Level Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (result.stressLevel) {
                            "Stress Rendah" -> Green.copy(alpha = 0.1f)
                            "Stress Sedang" -> Orange.copy(alpha = 0.1f)
                            "Stress Tinggi" -> Red.copy(alpha = 0.1f)
                            else -> Color.LightGray.copy(alpha = 0.1f)
                        }
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = result.stressLevel,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (result.stressLevel) {
                            "Stress Rendah" -> Green
                            "Stress Sedang" -> Orange
                            "Stress Tinggi" -> Red
                            else -> Color.Gray
                        }
                    )
                    Text(
                        text = "Hasil Assesment",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Score Reference
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Score Reference:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                ScoreReferenceRow("0.0 - 5.3", "Stress Rendah", Green)
                ScoreReferenceRow("5.3 - 10.6", "Stress Sedang", Orange)
                ScoreReferenceRow("10.7 - 16.0", "Stress Tinggi", Red)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Penilaian Anda telah disimpan. Anda dapat mengambil penilaian kembali dalam 30 hari.",
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = Color.Gray,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
        return
    }

    val currentQuestion = schema.questions[currentIndex]

    Column {

        Text(
            text = schema.description,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PROGRESS DOTS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(schema.questions.size) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (index == currentIndex) Black else Grey,
                            shape = CircleShape
                        )
                )
                if (index != schema.questions.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QUESTION
        Text(
            text = "${currentIndex + 1}. ${currentQuestion.text}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OPTIONS
        schema.scale.options.forEach { option ->

            val isSelected = answers[currentIndex] == option.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        answers[currentIndex] = option.value
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = isSelected,
                    onClick = {
                        answers[currentIndex] = option.value
                    }
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = option.label,
                    color = Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // NAVIGATION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // PREVIOUS
            if (currentIndex > 0) {
                Text(
                    text = "Kembali",
                    modifier = Modifier.clickable {
                        currentIndex--
                    },
                    color = Color.Gray
                )
            } else {
                Spacer(Modifier.width(1.dp))
            }

            // NEXT / FINISH
            Text(
                text = if (currentIndex == schema.questions.lastIndex) "Selesai" else "Lanjut",
                modifier = Modifier.clickable {
                    if (answers[currentIndex] == null) return@clickable

                    if (currentIndex < schema.questions.lastIndex) {
                        currentIndex++
                    } else {
                        val score = calculatePSSScore(schema, answers)
                        onScoreCalculated(score)
                        isFinished = true
                    }
                },
                fontWeight = FontWeight.Bold,
                color = Black
            )
        }
    }
}
