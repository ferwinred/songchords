package com.example.songchords.model

/**
 * Represents guitar string positions for a chord diagram.
 * frets: List of 6 integers for Low E (string 6) through High E (string 1).
 * -1 = muted string ('X')
 *  0 = open string ('O')
 * >0 = fret position
 * baseFret: starting fret number (1 = open position/nut)
 * fingers: optional finger numbers (1=index, 2=middle, 3=ring, 4=pinky, 0/muted=0)
 */
data class GuitarChordDiagram(
    val frets: List<Int>,
    val baseFret: Int = 1,
    val fingers: List<Int> = emptyList()
)

/**
 * Represents piano pitch classes (0=C, 1=C#, ..., 11=B) active in a chord.
 */
data class PianoChordNotes(
    val pitchClasses: List<Int>,
    val rootPitchClass: Int,
    val bassPitchClass: Int? = null
)

object ChordDiagramProvider {

    private val KNOWN_GUITAR_CHARTS = mapOf(
        // C Chords
        "C" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 1, 0), 1, listOf(0, 3, 2, 0, 1, 0)),
        "Cm" to GuitarChordDiagram(listOf(-1, 3, 5, 5, 4, 3), 3, listOf(0, 1, 3, 4, 2, 1)),
        "C7" to GuitarChordDiagram(listOf(-1, 3, 2, 3, 1, 0), 1, listOf(0, 3, 2, 4, 1, 0)),
        "Cmaj7" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 0, 0), 1, listOf(0, 3, 2, 0, 0, 0)),
        "Cadd2" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 3, 0), 1, listOf(0, 2, 1, 0, 3, 0)),
        "Csus4" to GuitarChordDiagram(listOf(-1, 3, 3, 0, 1, 1), 1, listOf(0, 3, 4, 0, 1, 1)),

        // D Chords
        "D" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 2), 1, listOf(0, 0, 0, 1, 3, 2)),
        "Dm" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 1), 1, listOf(0, 0, 0, 2, 3, 1)),
        "D7" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 1, 2), 1, listOf(0, 0, 0, 2, 1, 3)),
        "Dmaj7" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 2, 2), 1, listOf(0, 0, 0, 1, 1, 1)),
        "Dsus4" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 3), 1, listOf(0, 0, 0, 1, 2, 3)),

        // E Chords
        "E" to GuitarChordDiagram(listOf(0, 2, 2, 1, 0, 0), 1, listOf(0, 2, 3, 1, 0, 0)),
        "Em" to GuitarChordDiagram(listOf(0, 2, 2, 0, 0, 0), 1, listOf(0, 2, 3, 0, 0, 0)),
        "E7" to GuitarChordDiagram(listOf(0, 2, 0, 1, 0, 0), 1, listOf(0, 2, 0, 1, 0, 0)),
        "Emaj7" to GuitarChordDiagram(listOf(0, 2, 1, 1, 0, 0), 1, listOf(0, 3, 1, 2, 0, 0)),

        // F Chords
        "F" to GuitarChordDiagram(listOf(1, 3, 3, 2, 1, 1), 1, listOf(1, 3, 4, 2, 1, 1)),
        "Fm" to GuitarChordDiagram(listOf(1, 3, 3, 1, 1, 1), 1, listOf(1, 3, 4, 1, 1, 1)),
        "F7" to GuitarChordDiagram(listOf(1, 3, 1, 2, 1, 1), 1, listOf(1, 3, 1, 2, 1, 1)),
        "F#m" to GuitarChordDiagram(listOf(2, 4, 4, 2, 2, 2), 2, listOf(1, 3, 4, 1, 1, 1)),

        // G Chords
        "G" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 3), 1, listOf(3, 2, 0, 0, 0, 4)),
        "Gm" to GuitarChordDiagram(listOf(3, 5, 5, 3, 3, 3), 3, listOf(1, 3, 4, 1, 1, 1)),
        "G7" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 1), 1, listOf(3, 2, 0, 0, 0, 1)),
        "Gmaj7" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 2), 1, listOf(3, 2, 0, 0, 0, 1)),

        // A Chords
        "A" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 2, 0), 1, listOf(0, 0, 1, 2, 3, 0)),
        "Am" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 1, 0), 1, listOf(0, 0, 2, 3, 1, 0)),
        "A7" to GuitarChordDiagram(listOf(-1, 0, 2, 0, 2, 0), 1, listOf(0, 0, 2, 0, 3, 0)),
        "Amaj7" to GuitarChordDiagram(listOf(-1, 0, 2, 1, 2, 0), 1, listOf(0, 0, 2, 1, 3, 0)),

        // B Chords
        "B" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 4, 2), 2, listOf(0, 1, 2, 3, 4, 1)),
        "Bm" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 3, 2), 2, listOf(0, 1, 3, 4, 2, 1)),
        "B7" to GuitarChordDiagram(listOf(-1, 2, 1, 2, 0, 2), 1, listOf(0, 2, 1, 3, 0, 4)),

        // Bb / Eb / Ab
        "Bb" to GuitarChordDiagram(listOf(-1, 1, 3, 3, 3, 1), 1, listOf(0, 1, 2, 3, 4, 1)),
        "Eb" to GuitarChordDiagram(listOf(-1, -1, 1, 3, 4, 3), 1, listOf(0, 0, 1, 2, 4, 3)),
        "Ab" to GuitarChordDiagram(listOf(4, 6, 6, 5, 4, 4), 4, listOf(1, 3, 4, 2, 1, 1))
    )

    fun getGuitarDiagram(chord: Chord): GuitarChordDiagram {
        val rootStandard = chord.root.toStandardName(preferFlats = false)
        val fullSymbol = rootStandard + chord.suffix

        // 1. Direct chart lookup
        KNOWN_GUITAR_CHARTS[fullSymbol]?.let { return it }

        // 2. Base chord chart lookup (ignoring complex suffix extensions if available)
        val cleanSuffix = when {
            chord.suffix.startsWith("m") && !chord.suffix.startsWith("maj") -> "m"
            chord.suffix.startsWith("7") -> "7"
            chord.suffix.startsWith("maj7") -> "maj7"
            else -> ""
        }
        val simplifiedSymbol = rootStandard + cleanSuffix
        KNOWN_GUITAR_CHARTS[simplifiedSymbol]?.let { return it }

        // 3. Fallback: Algorithmic E-shape or A-shape barre chord transposition
        val pitch = chord.root.pitchClass
        val isMinor = chord.suffix.contains("m") && !chord.suffix.contains("maj")

        return if (isMinor) {
            // Em shape barre chord
            val fretOffset = (pitch - Note.E.pitchClass).mod(12)
            if (fretOffset == 0) {
                GuitarChordDiagram(listOf(0, 2, 2, 0, 0, 0), 1)
            } else {
                GuitarChordDiagram(listOf(fretOffset, fretOffset + 2, fretOffset + 2, fretOffset, fretOffset, fretOffset), fretOffset)
            }
        } else {
            // E shape barre chord
            val fretOffset = (pitch - Note.E.pitchClass).mod(12)
            if (fretOffset == 0) {
                GuitarChordDiagram(listOf(0, 2, 2, 1, 0, 0), 1)
            } else {
                GuitarChordDiagram(listOf(fretOffset, fretOffset + 2, fretOffset + 2, fretOffset + 1, fretOffset, fretOffset), fretOffset)
            }
        }
    }

    fun getPianoNotes(chord: Chord): PianoChordNotes {
        val rootPitch = chord.root.pitchClass
        val bassPitch = chord.bass?.pitchClass
        val pitches = mutableSetOf<Int>()

        pitches.add(rootPitch)

        val suffix = chord.suffix.lowercase()

        when {
            suffix.contains("m") && !suffix.contains("maj") && !suffix.contains("dim") -> {
                // Minor triad: root, minor 3rd (+3), 5th (+7)
                pitches.add((rootPitch + 3) % 12)
                pitches.add((rootPitch + 7) % 12)
            }
            suffix.contains("dim") -> {
                // Diminished triad: root, minor 3rd (+3), diminished 5th (+6)
                pitches.add((rootPitch + 3) % 12)
                pitches.add((rootPitch + 6) % 12)
            }
            suffix.contains("aug") || suffix.contains("+") -> {
                // Augmented triad: root, major 3rd (+4), augmented 5th (+8)
                pitches.add((rootPitch + 4) % 12)
                pitches.add((rootPitch + 8) % 12)
            }
            suffix.contains("sus4") -> {
                // Suspended 4th: root, 4th (+5), 5th (+7)
                pitches.add((rootPitch + 5) % 12)
                pitches.add((rootPitch + 7) % 12)
            }
            suffix.contains("sus2") -> {
                // Suspended 2nd: root, major 2nd (+2), 5th (+7)
                pitches.add((rootPitch + 2) % 12)
                pitches.add((rootPitch + 7) % 12)
            }
            else -> {
                // Default Major triad: root, major 3rd (+4), 5th (+7)
                pitches.add((rootPitch + 4) % 12)
                pitches.add((rootPitch + 7) % 12)
            }
        }

        // Add 7th extensions
        if (suffix.contains("maj7")) {
            pitches.add((rootPitch + 11) % 12)
        } else if (suffix.contains("7")) {
            pitches.add((rootPitch + 10) % 12)
        }

        // Add 2/9 extension
        if (suffix.contains("add2") || suffix.contains("add9") || suffix.contains("2") || suffix.contains("9")) {
            pitches.add((rootPitch + 2) % 12)
        }

        // Add slash bass note if specified
        bassPitch?.let { pitches.add(it) }

        return PianoChordNotes(
            pitchClasses = pitches.toList().sorted(),
            rootPitchClass = rootPitch,
            bassPitchClass = bassPitch
        )
    }
}
