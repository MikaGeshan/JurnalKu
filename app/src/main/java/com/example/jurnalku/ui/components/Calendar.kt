package com.example.jurnalku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.entries.MoodClass
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    moodHistory: Map<LocalDate, MoodClass> = emptyMap(),
    activityHistory: Map<LocalDate, List<Map<String, Any>>> = emptyMap()
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var clickedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth()
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey,
                        modifier = Modifier.width(40.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val firstDayOfMonth = currentMonth.atDay(1)
            val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            val daysInMonth = currentMonth.lengthOfMonth()

            val totalCells = startDayOfWeek + daysInMonth
            val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

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
                                val mood = moodHistory[date]
                                val activities = activityHistory[date] ?: emptyList()
                                val hasHistory = mood != null || activities.isNotEmpty()

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
                                            if (hasHistory) {
                                                clickedDate = date
                                                showBottomSheet = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        // mood indicator
                                        if (mood != null) {
                                            ComposableIcon(
                                                icon = mood.icon,
                                                tint = Color.Unspecified,
                                                size = 14.dp
                                            )
                                        }

                                        // tanggal
                                        Text(
                                            text = day.toString(),
                                            color = when {
                                                isSelected -> Color.White
                                                else -> MaterialTheme.colorScheme.onBackground
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                        
                                        // activity indicator
                                        if (activities.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .background(if (isSelected) Color.White else JungleGreen, CircleShape)
                                            )
                                        }
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

    if (showBottomSheet && clickedDate != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Grey.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            DaySummarySheet(
                date = clickedDate!!,
                mood = moodHistory[clickedDate!!],
                activities = activityHistory[clickedDate!!] ?: emptyList()
            )
        }
    }
}

@Composable
private fun DaySummarySheet(
    date: LocalDate,
    mood: MoodClass?,
    activities: List<Map<String, Any>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            style = MaterialTheme.typography.bodyMedium,
            color = Grey,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Activity History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (mood != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = mood.color.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, mood.color.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ComposableIcon(icon = mood.icon, size = 42.dp, tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mood was", style = MaterialTheme.typography.labelSmall, color = Grey)
                        Text(
                            mood.key.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = mood.color
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        activities.forEach { act ->
            val type = act["type"] as? String ?: ""
            val name = act["name"] as? String ?: "Journal"
            
            val (label, actionColor, icon) = when (type) {
                "JOURNAL_CREATED" -> Triple("Journal created: $name", JungleGreen, AppIconClass.Check)
                "JOURNAL_UPDATED" -> Triple("Journal updated: $name", Color(0xFF2196F3.toInt()), AppIconClass.Check)
                "JOURNAL_DELETED" -> Triple("Journal deleted: $name", Color.Red, AppIconClass.Check)
                else -> Triple("Action performed", Grey, AppIconClass.Check)
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                color = actionColor.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, actionColor.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(actionColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ComposableIcon(icon = icon, size = 20.dp, tint = actionColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Action", style = MaterialTheme.typography.labelSmall, color = Grey)
                        Text(
                            label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = actionColor
                        )
                    }
                }
            }
        }
        
        if (mood == null && activities.isEmpty()) {
            Text(
                text = "No history for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}
