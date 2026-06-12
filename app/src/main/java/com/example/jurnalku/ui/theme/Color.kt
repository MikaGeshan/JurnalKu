package com.example.jurnalku.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors
val White = Color(0xFFFFFFFF.toInt())
val Black = Color(0xFF000000.toInt())

// RGB
val Red = Color(0xFFBB0A1E.toInt())
val SoftOrange = Color(0xFFFFE0B2.toInt())
val Orange = Color(0xFFFFA500.toInt())
val Cream = Color(0xFFF5E6CC.toInt())
val Yellow = Color(0xFFFFFF00.toInt())
val Green = Color(0xFF008000.toInt())
val SoftBlue = Color(0xFFB0C4DE.toInt())
val Blue = Color(0xFF0000FF.toInt())
val Purple = Color(0xFF800080.toInt())

// Custom Colors
val SoftGreen = Color(0xFFC2D8B0.toInt())
var JungleGreen = Color(0xFF36561C.toInt())

val Purple80 = Color(0xFFD0BCFF.toInt())
val PurpleGrey80 = Color(0xFFCCC2DC.toInt())
val Pink80 = Color(0xFFEFB8C8.toInt())

val Purple40 = Color(0xFF6650a4.toInt())
val PurpleGrey40 = Color(0xFF625b71.toInt())
val Pink40 = Color(0xFF7D5260.toInt())

val Grey = Color(0xFFC1B7B7.toInt())

/**
 * Safely converts a Long (which may be sign-extended from an Int) into a Compose Color.
 * This prevents the ArrayIndexOutOfBoundsException in Color(ULong) by ensuring we only
 * pass the 32-bit ARGB part to Color(Int).
 */
fun safeColor(colorLong: Long): Color {
    return Color((colorLong and 0xFFFFFFFFL).toInt())
}
