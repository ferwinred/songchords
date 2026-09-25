package com.example.songchords.ui.tuner

import com.example.songchords.R
import kotlin.math.abs
import kotlin.math.ln

enum class InstrumentType(val titleRes: Int) {
    GUITAR_6(R.string.instrument_guitar),
    BASS_4(R.string.instrument_bass_4),
    BASS_5(R.string.instrument_bass_5)
}

data class TargetString(
    val number: Int,
    val noteName: String,
    val frequencyHz: Double
)

enum class TuningStatus {
    IN_TUNE,
    FLAT,
    SHARP
}

object TunerEngine {

    val GUITAR_STRINGS = listOf(
        TargetString(6, "E2", 82.41),
        TargetString(5, "A2", 110.00),
        TargetString(4, "D3", 146.83),
        TargetString(3, "G3", 196.00),
        TargetString(2, "B3", 246.94),
        TargetString(1, "E4", 329.63)
    )

    val BASS_4_STRINGS = listOf(
        TargetString(4, "E1", 41.20),
        TargetString(3, "A1", 55.00),
        TargetString(2, "D2", 73.42),
        TargetString(1, "G2", 98.00)
    )

    val BASS_5_STRINGS = listOf(
        TargetString(5, "B0", 30.87),
        TargetString(4, "E1", 41.20),
        TargetString(3, "A1", 55.00),
        TargetString(2, "D2", 73.42),
        TargetString(1, "G2", 98.00)
    )

    fun getStringsForInstrument(type: InstrumentType): List<TargetString> = when (type) {
        InstrumentType.GUITAR_6 -> GUITAR_STRINGS
        InstrumentType.BASS_4 -> BASS_4_STRINGS
        InstrumentType.BASS_5 -> BASS_5_STRINGS
    }

    /**
     * Calculates the cents offset between actual pitch and target pitch.
     * Formula: 1200 * log2(actualFreq / targetFreq)
     */
    fun calculateCentsOffset(actualFreq: Double, targetFreq: Double): Double {
        if (targetFreq <= 0.0 || actualFreq <= 0.0) return 0.0
        return 1200.0 * (ln(actualFreq / targetFreq) / ln(2.0))
    }

    /**
     * Given target frequency and a cents offset (-50 to +50),
     * calculates the resulting actual frequency.
     */
    fun frequencyFromCents(targetFreq: Double, centsOffset: Double): Double {
        return targetFreq * Math.pow(2.0, centsOffset / 1200.0)
    }

    /**
     * Determines tuning status based on cents offset.
     * Within +/- 5 cents is considered IN_TUNE.
     */
    fun getTuningStatus(centsOffset: Double, toleranceCents: Double = 5.0): TuningStatus {
        return when {
            abs(centsOffset) <= toleranceCents -> TuningStatus.IN_TUNE
            centsOffset < -toleranceCents -> TuningStatus.FLAT
            else -> TuningStatus.SHARP
        }
    }
}
