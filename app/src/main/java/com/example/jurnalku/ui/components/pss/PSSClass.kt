package com.example.jurnalku.ui.components.pss

import android.content.Context
import com.example.jurnalku.R
import com.google.gson.Gson

data class PSSSchema(
    val title: String,
    val description: String,
    val scale: Scale,
    val questions: List<Question>
)

data class Scale(
    val type: String,
    val options: List<Option>
)

data class Option(
    val label: String,
    val value: Int
)

data class Question(
    val id: Int,
    val text: String,
    val reverse_scored: Boolean
)

fun loadPSS(context: Context): PSSSchema {
    val inputStream = context.resources.openRawResource(R.raw.pss)
    val json = inputStream.bufferedReader().use { it.readText() }

    return Gson().fromJson(json, PSSSchema::class.java)
}