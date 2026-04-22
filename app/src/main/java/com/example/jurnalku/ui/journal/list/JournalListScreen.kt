package com.example.jurnalku.ui.journal.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun JournalListScreen(
    onNavigateEntries: () -> Unit,
    onNavigateCreateJournal: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onNavigateEntries,
            ) {
                Text("X", fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = { onNavigateCreateJournal() } ) {
                    ComposableIcon(
                        icon = AppIconClass.Add,
                        tint = JungleGreen,
                        size = 28.dp
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey,
                        contentColor = Black
                    )
                ) {
                    Text(
                        "Select",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { }) {
                    ComposableIcon(
                        icon = AppIconClass.Profile,
                        tint = JungleGreen,
                        size = 28.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}