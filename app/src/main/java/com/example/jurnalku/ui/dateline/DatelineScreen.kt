package com.example.jurnalku.ui.dateline

import MoodCounter
import MoodData
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.Calendar
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Red
import com.example.jurnalku.ui.theme.Yellow
import java.time.LocalDate

@Composable
fun DatelineScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Calendar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            MoodCounter(
                moods = listOf(
                    MoodData("Good", 10, Green),
                    MoodData("Meh", 5, Yellow),
                    MoodData("Awful", 2, Red)
                )
            )
        }
    }
}