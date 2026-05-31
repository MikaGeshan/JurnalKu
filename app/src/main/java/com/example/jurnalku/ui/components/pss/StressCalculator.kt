package com.example.jurnalku.ui.components.pss

import java.math.BigDecimal
import java.math.RoundingMode

data class StressResult(
    val averageMood: Double = 0.0,
    val convertedMood: Double = 0.0,
    val pss4Score: Int = 0,
    val finalScore: Double = 0.0,
    val stressLevel: String = "Belum Ada Data",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

object StressCalculator {

    fun calculate(
        pss4Score: Int?,
        moodValues: List<Int>?
    ): StressResult {

        return try {

            // SAFE DEFAULTS
            val safePssScore = (pss4Score ?: 0)
                .coerceIn(0, 16)

            val safeMoodValues = moodValues
                ?.filter { it in 1..4 }
                ?: emptyList()

            // Kalau belum ada data mood
            if (safeMoodValues.isEmpty()) {
                return StressResult(
                    pss4Score = safePssScore,
                    stressLevel = "Belum Ada Data",
                    isSuccess = false,
                    errorMessage = "Mood data kosong"
                )
            }

            val avgMood =
                calculateAverageMood(safeMoodValues)

            val convertedMood =
                convertMoodToPSSScale(avgMood)

            val finalScore = if (pss4Score == null) {
                convertedMood
            } else {
                calculateFinalStressScore(
                    safePssScore,
                    convertedMood
                )
            }

            val stressLevel =
                getStressLevel(finalScore)

            StressResult(
                averageMood = avgMood,
                convertedMood = convertedMood,
                pss4Score = safePssScore,
                finalScore = finalScore,
                stressLevel = stressLevel,
                isSuccess = true
            )

        } catch (e: Exception) {

            StressResult(
                stressLevel = "Error",
                isSuccess = false,
                errorMessage = e.message
            )
        }
    }

    fun calculateAverageMood(
        moodValues: List<Int>
    ): Double {

        if (moodValues.isEmpty()) return 0.0

        return roundToTwoDecimals(
            moodValues.average()
        )
    }

    fun convertMoodToPSSScale(
        averageMood: Double
    ): Double {

        if (averageMood <= 0.0) return 0.0

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

    fun getStressLevel(
        score: Double
    ): String {

        return when {
            score == 0.0 -> "Belum Ada Data"
            score < 5.34 -> "Stress Rendah"
            score < 10.67 -> "Stress Sedang"
            else -> "Stress Tinggi"
        }
    }

    private fun roundToTwoDecimals(
        value: Double
    ): Double {

        return try {
            BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
}