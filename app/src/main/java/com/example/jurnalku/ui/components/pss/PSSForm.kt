package com.example.jurnalku.ui.components.pss

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun PSSForm() {

    val context = LocalContext.current
    val schema = remember { loadPSS(context) }

    var currentIndex by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val answers = remember {
        mutableStateListOf<Int?>().apply {
            repeat(schema.questions.size) { add(null) }
        }
    }

    if (isFinished) {
        val score = calculatePSSScore(schema, answers)
        val result = getPSSInterpretation(schema, score)

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hasil Penilaian PSS-4",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Total Skor: $score",
                fontSize = 18.sp,
                color = Black
            )
            Text(
                text = result,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (score >= 12) Color.Red else if (score >= 6) Color(0xFFFFA500) else Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    currentIndex = 0
                    answers.clear()
                    repeat(schema.questions.size) { answers.add(null) }
                    isFinished = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("Ulangi Tes", color = Color.White)
            }
        }
        return
    }

    val currentQuestion = schema.questions[currentIndex]

    Column {

        Text(
            text = schema.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                        isFinished = true
                    }
                },
                fontWeight = FontWeight.Bold,
                color = Black
            )
        }
    }
}
