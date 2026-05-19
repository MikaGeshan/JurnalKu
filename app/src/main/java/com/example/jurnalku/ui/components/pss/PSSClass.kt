package com.example.jurnalku.ui.components.pss

import android.content.Context
import com.example.jurnalku.R
import com.google.gson.Gson

data class PSSSchema(
    val title: String,
    val description: String,
    val scale: Scale,
    val questions: List<Question>,
    val scoring: Scoring
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

data class Scoring(
    val min_score: Int,
    val max_score: Int,
    val interpretation: List<Interpretation>
)

data class Interpretation(
    val range: List<Int>,
    val label: String
)

fun loadPSS(context: Context): PSSSchema {
    val inputStream = context.resources.openRawResource(R.raw.pss)
    val json = inputStream.bufferedReader().use { it.readText() }

    return Gson().fromJson(json, PSSSchema::class.java)
}

fun calculatePSSScore(schema: PSSSchema, answers: List<Int?>): Int {
    var total = 0
    schema.questions.forEachIndexed { index, question ->
        val answer = answers[index] ?: 0
        total += if (question.reverse_scored) 4 - answer else answer
    }
    return total
}

fun getPSSInterpretation(schema: PSSSchema, score: Int): String {
    return schema.scoring.interpretation.find {
        score >= it.range[0] && score <= it.range[1]
    }?.label ?: "Unknown"
}
