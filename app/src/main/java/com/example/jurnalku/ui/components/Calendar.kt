package com.example.jurnalku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.White
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

val moodMap = mapOf(
    LocalDate.of(2026, 4, 1) to "😄",
    LocalDate.of(2026, 4, 2) to "🙂",
    LocalDate.of(2026, 4, 3) to "😐",
    LocalDate.of(2026, 4, 4) to "😟",
    LocalDate.of(2026, 4, 5) to "😫",
    LocalDate.of(2026, 4, 10) to "😄",
    LocalDate.of(2026, 4, 15) to "🙂"
)

@Composable
fun Calendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // bulan
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) }
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) }
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // hari tanggal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val firstDayOfMonth = currentMonth.atDay(1)
            val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            val daysInMonth = currentMonth.lengthOfMonth()

            val totalCells = startDayOfWeek + daysInMonth
            val rows = (totalCells / 7) + 1

            Column {
                var day = 1

                for (i in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        for (j in 0..6) {

                            val cellIndex = i * 7 + j

                            if (cellIndex < startDayOfWeek || day > daysInMonth) {
                                Box(modifier = Modifier.size(40.dp))
                            } else {

                                val date = currentMonth.atDay(day)
                                val mood = moodMap[date]

                                val isToday = date == today
                                val isSelected = date == selectedDate

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> JungleGreen
                                                isToday -> JungleGreen.copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            onDateSelected(date)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        // mood history
                                        if (mood != null) {
                                            Text(
                                                text = mood,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        // tanggal
                                        Text(
                                            text = day.toString(),
                                            color = when {
                                                isSelected -> Color.White
                                                else -> MaterialTheme.colorScheme.onBackground
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                day++
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}