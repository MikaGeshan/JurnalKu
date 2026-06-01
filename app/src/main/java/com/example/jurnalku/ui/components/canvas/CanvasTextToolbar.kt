package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.JungleGreen

@Composable
fun TextToolbar(
    selectedFontFamily: FontFamily,
    onFontFamilyChange: (FontFamily) -> Unit,
    textAlign: TextAlign,
    onTextAlignChange: (TextAlign) -> Unit,
    isUnderlined: Boolean,
    onUnderlineChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamilies = listOf(
        "Default" to FontFamily.Default,
        "Serif" to FontFamily.Serif,
        "SansSerif" to FontFamily.SansSerif,
        "Monospace" to FontFamily.Monospace
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF5F5F5),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Font Selector
                fontFamilies.forEach { (name, family) ->
                    val isSelected = selectedFontFamily == family
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) JungleGreen else Color.White,
                        modifier = Modifier.clickable { onFontFamilyChange(family) }
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isSelected) Color.White else Black,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = family
                        )
                    }
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Underline Toggle
                IconButton(
                    onClick = { onUnderlineChange(!isUnderlined) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isUnderlined) JungleGreen else Color.Transparent,
                        contentColor = if (isUnderlined) Color.White else Black
                    )
                ) {
                    Text(
                        "U",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Text Alignments
                IconButton(
                    onClick = { onTextAlignChange(TextAlign.Left) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (textAlign == TextAlign.Left) JungleGreen else Color.Transparent,
                        contentColor = if (textAlign == TextAlign.Left) Color.White else Black
                    )
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Left")
                }

                IconButton(
                    onClick = { onTextAlignChange(TextAlign.Center) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (textAlign == TextAlign.Center) JungleGreen else Color.Transparent,
                        contentColor = if (textAlign == TextAlign.Center) Color.White else Black
                    )
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Center")
                }

                IconButton(
                    onClick = { onTextAlignChange(TextAlign.Right) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (textAlign == TextAlign.Right) JungleGreen else Color.Transparent,
                        contentColor = if (textAlign == TextAlign.Right) Color.White else Black
                    )
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Right")
                }
            }
        }
    }
}
