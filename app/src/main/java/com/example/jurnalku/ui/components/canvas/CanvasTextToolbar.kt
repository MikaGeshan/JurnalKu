package com.example.jurnalku.ui.components.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    isStrikethrough: Boolean,
    onStrikethroughChange: (Boolean) -> Unit,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamilies = listOf(
        FontFamily.Default,
        FontFamily.Serif,
        FontFamily.SansSerif,
        FontFamily.Monospace
    )

    val colors = listOf(
        Black,
        Color(0xFFF44336.toInt()), // Red
        Color(0xFFFF9800.toInt()), // Orange
        Color(0xFFFFEB3B.toInt()), // Yellow
        Color(0xFF4CAF50.toInt()), // Green
        Color(0xFF2196F3.toInt()), // Blue
        Color(0xFF9C27B0.toInt()), // Purple
        Color(0xFF795548.toInt()), // Brown
        Color(0xFF9E9E9E.toInt())  // Gray
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF5F5F5.toInt()),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            // Style and Alignment Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Font Family Button
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        val currentIndex = fontFamilies.indexOf(selectedFontFamily)
                        val nextIndex = (currentIndex + 1) % fontFamilies.size
                        onFontFamilyChange(fontFamilies[nextIndex])
                    }
                ) {
                    Icon(
                        painter = painterResource(id = AppIconClass.font_style.resId),
                        contentDescription = "Change Font",
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Font Size Control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = { onFontSizeChange((fontSize - 2).coerceAtLeast(8f)) }
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = fontSize.toInt().toString(),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    )
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = { onFontSizeChange((fontSize + 2).coerceAtMost(72f)) }
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Bold Toggle
                ToolbarToggleButton(
                    text = "B",
                    isSelected = isBold,
                    onClick = { onBoldChange(!isBold) },
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                // Italic Toggle
                ToolbarToggleButton(
                    text = "I",
                    isSelected = isItalic,
                    onClick = { onItalicChange(!isItalic) },
                    style = TextStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
                )

                // Underline Toggle
                ToolbarToggleButton(
                    text = "U",
                    isSelected = isUnderlined,
                    onClick = { onUnderlineChange(!isUnderlined) },
                    style = TextStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)
                )

                // Strikethrough Toggle
                ToolbarToggleButton(
                    text = "S",
                    isSelected = isStrikethrough,
                    onClick = { onStrikethroughChange(!isStrikethrough) },
                    style = TextStyle(textDecoration = TextDecoration.LineThrough, fontWeight = FontWeight.Bold)
                )

                VerticalDivider(modifier = Modifier.height(24.dp))

                // Alignment Buttons
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

            Spacer(modifier = Modifier.height(12.dp))

            // Color Palette Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 32.dp else 24.dp)
                            .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                            .padding(if (isSelected) 2.dp else 0.dp)
                            .let {
                                if (isSelected) it.border(2.dp, JungleGreen, androidx.compose.foundation.shape.CircleShape) else it
                            }
                            .clickable { onColorChange(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    style: TextStyle
) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = if (isSelected) JungleGreen else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = style.copy(
                    fontSize = 18.sp,
                    color = if (isSelected) Color.White else Black
                )
            )
        }
    }
}
