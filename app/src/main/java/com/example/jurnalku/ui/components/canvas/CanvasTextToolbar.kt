package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jurnalku.ui.components.icon.AppIconClass
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
    isBold: Boolean,
    onBoldChange: (Boolean) -> Unit,
    isItalic: Boolean,
    onItalicChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamilies = listOf(
        FontFamily.Default,
        FontFamily.Serif,
        FontFamily.SansSerif,
        FontFamily.Monospace
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF5F5F5),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        val currentIndex = fontFamilies.indexOf(selectedFontFamily)
                        val nextIndex = (currentIndex + 1) % fontFamilies.size
                        onFontFamilyChange(fontFamilies[nextIndex])
                    }
                ) {
                    val icon = AppIconClass.font_style
                    Icon(
                        painter = painterResource(id = icon.resId),
                        contentDescription = "Change Font",
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Bold Toggle
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = { onBoldChange(!isBold) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isBold) JungleGreen else Color.Transparent,
                        contentColor = if (isBold) Color.White else Black
                    )
                ) {
                    Text(
                        "B",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                // Italic Toggle
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = { onItalicChange(!isItalic) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isItalic) JungleGreen else Color.Transparent,
                        contentColor = if (isItalic) Color.White else Black
                    )
                ) {
                    Text(
                        "I",
                        style = TextStyle(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                // Underline Toggle
                IconButton(
                    modifier = Modifier.size(40.dp),
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
                            fontSize = 16.sp
                        )
                    )
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Separate Alignment Buttons
                val alignments = listOf(
                    TextAlign.Left to AppIconClass.Align_Left,
                    TextAlign.Center to AppIconClass.Align_Center,
                    TextAlign.Right to AppIconClass.Align_Right
                )

                alignments.forEach { (align, icon) ->
                    val isSelected = textAlign == align
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { onTextAlignChange(align) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isSelected) JungleGreen else Color.Transparent,
                            contentColor = if (isSelected) Color.White else Black
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = icon.resId),
                            contentDescription = align.toString(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
