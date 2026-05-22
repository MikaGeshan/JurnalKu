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

    /**
     * Main calculation engine that returns a structured StressResult.
     */
    fun calculate(pss4Score: Int, moodValues: List<Int>): StressResult {
        // 1. Validation
        val validPssScore = pss4Score.coerceIn(0, 16)
        val validMoodValues = moodValues.filter { it in 1..4 }

        // 2. Calculation Steps
        val avgMood = calculateAverageMood(validMoodValues)
        val convertedMood = convertMoodToPSSScale(avgMood)
        val finalScore = calculateFinalStressScore(validPssScore, convertedMood)
        val level = getStressLevel(finalScore)

        return StressResult(
            averageMood = avgMood,
            convertedMood = convertedMood,
            pss4Score = validPssScore,
            finalScore = finalScore,
            stressLevel = level
        )
    }

    /**
     * Calculates average based on available data (modular).
     */
    fun calculateAverageMood(moodValues: List<Int>): Double {
        if (moodValues.isEmpty()) return 1.0
        return roundToTwoDecimals(moodValues.average())
    }

    /**
     * Calculates total valid moods.
     */
    fun calculateTotalMoods(moodValues: List<Int>): Int {
        return moodValues.filter { it in 1..4 }.size
    }

    /**
     * Normalizes mood (1-4) to PSS scale (0-16).
     */
    fun convertMoodToPSSScale(averageMood: Double): Double {
        val normalized = ((averageMood - 1.0) / (4.0 - 1.0)) * 16.0
        return roundToTwoDecimals(normalized)
    }

    /**
     * Averages PSS and Converted Mood scores.
     */
    fun calculateFinalStressScore(pssScore: Int, convertedMood: Double): Double {
        val score = (pssScore.toDouble() + convertedMood) / 2.0
        return roundToTwoDecimals(score)
    }

    /**
     * Maps the final score to a Indonesian category string.
     */
    fun getStressLevel(score: Double): String {
        return when {
            score <= 5.0 -> "Stress Rendah"
            score <= 10.0 -> "Stress Sedang"
            else -> "Stress Tinggi"
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return if (value.isNaN() || value.isInfinite()) 0.0 else {
            BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
    }
}
