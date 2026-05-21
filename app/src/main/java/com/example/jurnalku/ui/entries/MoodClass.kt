package com.example.jurnalku.ui.entries

import androidx.compose.ui.graphics.Color
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.theme.Green
import com.example.jurnalku.ui.theme.Orange
import com.example.jurnalku.ui.theme.Red
import com.example.jurnalku.ui.theme.SoftGreen

sealed class MoodClass(
    val key: String,
    val icon: AppIconClass,
    val value: Int,
    val emoji: String,
    val color: Color
) {
    object VerySad  : MoodClass("VERY_SAD",   AppIconClass.MoodVerySad,   4, "😫", Red)
    object Sad      : MoodClass("SAD",        AppIconClass.MoodSad,       3, "😟", Orange)
    object Happy    : MoodClass("HAPPY",      AppIconClass.MoodHappy,     2, "🙂", SoftGreen)
    object VeryHappy: MoodClass("VERY_HAPPY", AppIconClass.MoodVeryHappy, 1, "😄", Green)

    companion object {
        val all = listOf(VeryHappy, Happy, Sad, VerySad)
        
        fun getEmoji(key: String?): String? {
            return all.find { it.key == key }?.emoji
        }
    }
}
