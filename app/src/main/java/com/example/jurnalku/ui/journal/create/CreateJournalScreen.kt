package com.example.jurnalku.ui.journal.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import com.example.jurnalku.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.canvas.CustomCanvas
import com.example.jurnalku.ui.theme.JungleGreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.jurnalku.ui.components.PaperTypePreview


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CreateJournalScreen(
    onBackToEntries: () -> Unit,
    onCancelCreateJournal: () -> Unit)
{

    var selectedColor by remember { mutableStateOf(Color.White) }
    var selectedType by remember { mutableStateOf("Blank") }
    var showCanvas by remember { mutableStateOf(false) }

    if (showCanvas) {
        CustomCanvas(
            paperColor = selectedColor,
            paperType = selectedType,
            onClose = onCancelCreateJournal
        )
        return
    }

    val colorOptions = listOf(
        Color.White,
        Color(0xFFF5E6CC),
        Color(0xFFFFE0B2),
        Color(0xFFDFF5E1)
    )

    val paperTypes = listOf(
        "Blank",
        "Dot Grid",
        "Grid",
        "Lined"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // HEADER
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            IconButton(
                onClick = onBackToEntries,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("X", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Choose Page Style",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // COLOR PALETTE
        Text("Paper Color", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier
            .height(12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            colorOptions.forEach { color ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(48.dp)
                        .background(color, RoundedCornerShape(12.dp))
                        .then(
                            if (selectedColor == color)
                                Modifier.border(2.dp, JungleGreen, RoundedCornerShape(12.dp))
                            else Modifier
                        )
                        .clickable { selectedColor = color }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PAPER TYPE
        Text("Paper Type", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            paperTypes.chunked(2).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    rowItems.forEach { type ->

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedType = type
                                    showCanvas = true
                                }
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(260.dp)
                                    .border(
                                        width = if (selectedType == type) 2.dp else 0.dp,
                                        color = JungleGreen,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                PaperTypePreview(
                                    type = type,
                                    color = selectedColor,
                                    isSelected = selectedType == type,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = type,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

    }
}