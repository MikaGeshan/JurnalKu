package com.example.jurnalku.ui.components.pss

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.Grey

@Composable
fun PSSForm() {

    val context = LocalContext.current
    val schema = remember { loadPSS(context) }

    var currentIndex by remember { mutableStateOf(0) }

    val answers = remember {
        mutableStateListOf<Int?>().apply {
            repeat(schema.questions.size) { add(null) }
        }
    }

    val currentQuestion = schema.questions[currentIndex]

    Column {

        Text(schema.description)

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // QUESTION
        Text(currentQuestion.text)

        Spacer(modifier = Modifier.height(12.dp))

        // OPTIONS
        schema.scale.options.forEach { option ->

            val isSelected = answers[currentIndex] == option.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        answers[currentIndex] = option.value
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = isSelected,
                    onClick = {
                        answers[currentIndex] = option.value
                    }
                )

                Spacer(Modifier.width(8.dp))

                Text(option.label)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NAVIGATION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // PREVIOUS
            if (currentIndex > 0) {
                Text(
                    text = "Previous",
                    modifier = Modifier.clickable {
                        currentIndex--
                    }
                )
            } else {
                Spacer(Modifier.width(1.dp))
            }

            // NEXT / FINISH
            Text(
                text = if (currentIndex == schema.questions.lastIndex) "Finish" else "Next",
                modifier = Modifier.clickable {

                    if (answers[currentIndex] == null) return@clickable

                    if (currentIndex < schema.questions.lastIndex) {
                        currentIndex++
                    } else {
                        println("DONE: $answers")
                    }
                },
                color = Black
            )
        }
    }
}