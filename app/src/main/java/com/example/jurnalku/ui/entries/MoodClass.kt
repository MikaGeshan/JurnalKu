package com.example.jurnalku.ui.entries

import com.example.jurnalku.ui.components.icon.AppIconClass

sealed class MoodClass(
    val key: String,
    val icon: AppIconClass,
    val value: Int
) {
    object VerySad  : MoodClass("VERY_SAD",   AppIconClass.MoodVerySad,   4)
    object Sad      : MoodClass("SAD",        AppIconClass.MoodSad,       3)
    object Happy    : MoodClass("HAPPY",      AppIconClass.MoodHappy,     2)
    object VeryHappy: MoodClass("VERY_HAPPY", AppIconClass.MoodVeryHappy, 1)

    companion object {
        val all = listOf(VerySad, Sad, Happy, VeryHappy)
    }
}