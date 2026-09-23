package com.example.songchords.model

/**
 * Represents a chord position at a specific character index in plainText lyrics.
 */
data class ChordPosition(
    val chord: Chord,
    val charIndex: Int
)

/**
 * Represents a single line in a song's lyrics/chords structure.
 */
sealed class ChordLyricsLine {
    data class SectionHeader(val title: String) : ChordLyricsLine()
    data class ChordLyrics(
        val plainText: String,
        val chordPositions: List<ChordPosition>
    ) : ChordLyricsLine()
    object EmptyLine : ChordLyricsLine()
}

/**
 * High-level parsed representation of a song with current transposition & notation states.
 */
data class ParsedSong(
    val song: Song,
    val lines: List<ChordLyricsLine>,
    val transpositionSemitones: Int = 0,
    val notationSystem: NotationSystem = NotationSystem.STANDARD,
    val preferFlats: Boolean = Key.parse(song.originalKey)?.preferFlats ?: false
) {
    val currentKey: Key?
        get() {
            val baseKey = Key.parse(song.originalKey) ?: return null
            return baseKey.transpose(transpositionSemitones)
        }

    fun transpose(semitonesDelta: Int): ParsedSong {
        val newSemitones = transpositionSemitones + semitonesDelta
        val effectivePreferFlats = currentKey?.preferFlats ?: preferFlats
        val updatedLines = lines.map { line ->
            when (line) {
                is ChordLyricsLine.ChordLyrics -> {
                    ChordLyricsLine.ChordLyrics(
                        plainText = line.plainText,
                        chordPositions = line.chordPositions.map { cp ->
                            cp.copy(chord = cp.chord.transpose(semitonesDelta, effectivePreferFlats))
                        }
                    )
                }
                else -> line
            }
        }
        return copy(
            lines = updatedLines,
            transpositionSemitones = newSemitones
        )
    }

    fun withNotationSystem(targetNotation: NotationSystem): ParsedSong {
        return copy(notationSystem = targetNotation)
    }

    /**
     * Reconstructs chord-tagged lyrics text in current transposition and notation.
     */
    fun toChordTaggedText(): String {
        return lines.joinToString("\n") { line ->
            when (line) {
                is ChordLyricsLine.SectionHeader -> "[${line.title}]"
                is ChordLyricsLine.EmptyLine -> ""
                is ChordLyricsLine.ChordLyrics -> {
                    if (line.chordPositions.isEmpty()) {
                        line.plainText
                    } else {
                        val sorted = line.chordPositions.sortedBy { it.charIndex }
                        val sb = StringBuilder()
                        var lastIdx = 0
                        val grouped = sorted.groupBy { it.charIndex }
                        for ((idx, posList) in grouped) {
                            val clampedIdx = idx.coerceIn(0, line.plainText.length)
                            if (clampedIdx > lastIdx) {
                                sb.append(line.plainText.substring(lastIdx, clampedIdx))
                                lastIdx = clampedIdx
                            }
                            for (cp in posList) {
                                sb.append("[").append(cp.chord.formatted(notationSystem, preferFlats)).append("]")
                            }
                        }
                        if (lastIdx < line.plainText.length) {
                            sb.append(line.plainText.substring(lastIdx))
                        }
                        sb.toString()
                    }
                }
            }
        }
    }
}
