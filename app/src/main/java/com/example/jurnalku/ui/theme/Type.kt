package com.example.jurnalku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jurnalku.R

// Set of Material typography styles to start with

val RobotoSerif = FontFamily(
    Font(R.font.robotoserif, FontWeight.Medium)
)
val Typography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = RobotoSerif),
        displayMedium = displayMedium.copy(fontFamily = RobotoSerif),
        displaySmall = displaySmall.copy(fontFamily = RobotoSerif),

        headlineLarge = headlineLarge.copy(fontFamily = RobotoSerif),
        headlineMedium = headlineMedium.copy(fontFamily = RobotoSerif),
        headlineSmall = headlineSmall.copy(fontFamily = RobotoSerif),

        titleLarge = titleLarge.copy(fontFamily = RobotoSerif),
        titleMedium = titleMedium.copy(fontFamily = RobotoSerif),
        titleSmall = titleSmall.copy(fontFamily = RobotoSerif),

        bodyLarge = bodyLarge.copy(fontFamily = RobotoSerif),
        bodyMedium = bodyMedium.copy(fontFamily = RobotoSerif),
        bodySmall = bodySmall.copy(fontFamily = RobotoSerif),

        labelLarge = labelLarge.copy(fontFamily = RobotoSerif),
        labelMedium = labelMedium.copy(fontFamily = RobotoSerif),
        labelSmall = labelSmall.copy(fontFamily = RobotoSerif)
    )
}