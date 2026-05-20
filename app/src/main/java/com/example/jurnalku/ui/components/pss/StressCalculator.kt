package com.example.jurnalku.ui.components.pss

import kotlin.math.round

object StressCalculator {

    /**
     * Calculates the average of mood values for a week.
     * Mood values are typically 1 (Very Happy) to 4 (Very Sad).
     */
    fun calculateAverageMood(moodValues: List<Int>): Double {
        if (moodValues.isEmpty()) return 1.0
        return moodValues.average()
    }

    /**
     * Converts the average mood (scale 1-4) to the PSS scale (0-16).
     * Formula: ((Avg - 1) / (4 - 1)) * 16
     */
    fun convertMoodToPSSScale(averageMood: Double): Double {
        val converted = ((averageMood - 1.0) / (4.0 - 1.0)) * 16.0
        return round(converted * 100) / 100.0 // Round to 2 decimal places
    }

    /**
     * Calculates the final stress score by averaging the PSS-4 score
     * and the converted daily mood score.
     * Formula: (PSS Score + Converted Mood) / 2
     */
    fun calculateFinalStressScore(pssScore: Int, convertedMood: Double): Double {
        val finalScore = (pssScore.toDouble() + convertedMood) / 2.0
        return round(finalScore * 100) / 100.0 // Round to 2 decimal places
    }
}
