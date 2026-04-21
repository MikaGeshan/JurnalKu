package com.example.jurnalku.ui.entries

import com.example.jurnalku.ui.components.icon.AppIconClass

sealed class MoodClass(
    val key: String,
    val icon: AppIconClass
) {
    object VerySad : MoodClass("VERY_SAD", AppIconClass.MoodVerySad)
    object Sad : MoodClass("SAD", AppIconClass.MoodSad)
    object Happy : MoodClass("HAPPY", AppIconClass.MoodHappy)
    object VeryHappy : MoodClass("VERY_HAPPY", AppIconClass.MoodVeryHappy)

    companion object {
        val all = listOf(VerySad, Sad, Happy, VeryHappy)
    }
}