package com.example.songchords.engine

import com.example.songchords.model.Chord
import com.example.songchords.model.Key
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song

object TranspositionEngine {

    const val SEMITONES_PER_FULL_TONE = 2
    const val SEMITONES_PER_HALF_TONE = 1

    fun transpose(chord: Chord, semitones: Int, preferFlats: Boolean = false): Chord {
        return chord.transpose(semitones, preferFlats)
    }

    fun transposeByTones(chord: Chord, fullTones: Double, preferFlats: Boolean = false): Chord {
        val semitones = (fullTones * SEMITONES_PER_FULL_TONE).toInt()
        return chord.transpose(semitones, preferFlats)
    }

    fun transposeFullTone(chord: Chord, steps: Int = 1, preferFlats: Boolean = false): Chord {
        return chord.transpose(steps * SEMITONES_PER_FULL_TONE, preferFlats)
    }

    fun transposeHalfTone(chord: Chord, steps: Int = 1, preferFlats: Boolean = false): Chord {
        return chord.transpose(steps * SEMITONES_PER_HALF_TONE, preferFlats)
    }

    fun transposeKey(key: Key, semitones: Int): Key {
        return key.transpose(semitones)
    }

    fun transposeParsedSong(parsedSong: ParsedSong, semitonesDelta: Int): ParsedSong {
        return parsedSong.transpose(semitonesDelta)
    }

    /**
     * Transposes raw chord-tagged text directly by semitones.
     */
    fun transposeText(
        content: String,
        semitones: Int,
        notationSystem: NotationSystem = NotationSystem.STANDARD,
        preferFlats: Boolean = false
    ): String {
        val dummySong = Song(
            title = "", artist = "", originalKey = "C", content = content
        )
        val parsed = ChordParser.parseSong(dummySong, notationSystem)
        val transposed = parsed.transpose(semitones)
        return transposed.toChordTaggedText()
    }
}
