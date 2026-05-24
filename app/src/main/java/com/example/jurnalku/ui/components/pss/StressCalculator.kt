package com.example.jurnalku.ui.components.pss

import java.math.BigDecimal
import java.math.RoundingMode

data class StressResult(
    val averageMood: Double,
    val convertedMood: Double,
    val pss4Score: Int,
    val finalScore: Double,
    val stressLevel: String
)

object StressCalculator {

    fun calculate(
        pss4Score: Int,
        moodValues: List<Int>
    ): StressResult {

        require(pss4Score in 0..16) {
            "PSS-4 score must be between 0 and 16"
        }

        require(moodValues.isNotEmpty()) {
            "Mood values cannot be empty"
        }

        require(moodValues.all { it in 1..4 }) {
            "Mood values must be between 1 and 4"
        }

        val avgMood = calculateAverageMood(moodValues)
        val convertedMood = convertMoodToPSSScale(avgMood)
        val finalScore = calculateFinalStressScore(
            pss4Score,
            convertedMood
        )

        return StressResult(
            averageMood = avgMood,
            convertedMood = convertedMood,
            pss4Score = pss4Score,
            finalScore = finalScore,
            stressLevel = getStressLevel(finalScore)
        )
    }

    fun calculateAverageMood(
        moodValues: List<Int>
    ): Double {
        return roundToTwoDecimals(moodValues.average())
    }

    fun convertMoodToPSSScale(
        averageMood: Double
    ): Double {

        val normalized =
            ((averageMood - 1.0) / 3.0) * 16.0

        return roundToTwoDecimals(normalized)
    }

    fun calculateFinalStressScore(
        pssScore: Int,
        convertedMood: Double
    ): Double {

        val score =
            (pssScore + convertedMood) / 2.0

        return roundToTwoDecimals(score)
    }

    fun getStressLevel(score: Double): String {
        return when {
            score < 5.34 -> "Stress Rendah"
            score < 10.67 -> "Stress Sedang"
            else -> "Stress Tinggi"
        }
    }

    private fun roundToTwoDecimals(
        value: Double
    ): Double {

        return BigDecimal(value)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }
}