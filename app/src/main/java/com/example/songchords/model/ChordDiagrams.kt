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
        "Cm7" to GuitarChordDiagram(listOf(-1, 3, 5, 3, 4, 3), 3, listOf(0, 1, 3, 1, 2, 1)),
        "Cmaj7" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 0, 0), 1, listOf(0, 3, 2, 0, 0, 0)),
        "Csus2" to GuitarChordDiagram(listOf(-1, 3, 0, 0, 1, 0), 1, listOf(0, 3, 0, 0, 1, 0)),
        "Csus4" to GuitarChordDiagram(listOf(-1, 3, 3, 0, 1, 1), 1, listOf(0, 3, 4, 0, 1, 1)),
        "Cadd9" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 3, 0), 1, listOf(0, 2, 1, 0, 3, 0)),
        "Cadd2" to GuitarChordDiagram(listOf(-1, 3, 2, 0, 3, 0), 1, listOf(0, 2, 1, 0, 3, 0)),
        "Cdim" to GuitarChordDiagram(listOf(-1, 3, 4, 2, 4, -1), 1, listOf(0, 2, 3, 1, 4, 0)),
        "Caug" to GuitarChordDiagram(listOf(-1, 3, 2, 1, 1, 0), 1, listOf(0, 4, 3, 1, 2, 0)),

        // D Chords
        "D" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 2), 1, listOf(0, 0, 0, 1, 3, 2)),
        "Dm" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 1), 1, listOf(0, 0, 0, 2, 3, 1)),
        "D7" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 1, 2), 1, listOf(0, 0, 0, 2, 1, 3)),
        "Dm7" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 1, 1), 1, listOf(0, 0, 0, 2, 1, 1)),
        "Dmaj7" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 2, 2), 1, listOf(0, 0, 0, 1, 1, 1)),
        "Dsus2" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 0), 1, listOf(0, 0, 0, 1, 2, 0)),
        "Dsus4" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 3), 1, listOf(0, 0, 0, 1, 2, 3)),
        "Dadd9" to GuitarChordDiagram(listOf(-1, -1, 0, 2, 3, 0), 1, listOf(0, 0, 0, 1, 2, 0)),
        "Ddim" to GuitarChordDiagram(listOf(-1, -1, 0, 1, 3, 1), 1, listOf(0, 0, 0, 1, 3, 2)),
        "Daug" to GuitarChordDiagram(listOf(-1, -1, 0, 3, 3, 2), 1, listOf(0, 0, 0, 2, 3, 1)),

        // E Chords
        "E" to GuitarChordDiagram(listOf(0, 2, 2, 1, 0, 0), 1, listOf(0, 2, 3, 1, 0, 0)),
        "Em" to GuitarChordDiagram(listOf(0, 2, 2, 0, 0, 0), 1, listOf(0, 2, 3, 0, 0, 0)),
        "E7" to GuitarChordDiagram(listOf(0, 2, 0, 1, 0, 0), 1, listOf(0, 2, 0, 1, 0, 0)),
        "Em7" to GuitarChordDiagram(listOf(0, 2, 0, 0, 0, 0), 1, listOf(0, 2, 0, 0, 0, 0)),
        "Emaj7" to GuitarChordDiagram(listOf(0, 2, 1, 1, 0, 0), 1, listOf(0, 3, 1, 2, 0, 0)),
        "Esus2" to GuitarChordDiagram(listOf(0, 2, 4, 1, 0, 0), 1, listOf(0, 1, 3, 2, 0, 0)),
        "Esus4" to GuitarChordDiagram(listOf(0, 2, 2, 2, 0, 0), 1, listOf(0, 2, 3, 4, 0, 0)),
        "Eadd9" to GuitarChordDiagram(listOf(0, 2, 2, 1, 0, 2), 1, listOf(0, 2, 3, 1, 0, 4)),
        "Edim" to GuitarChordDiagram(listOf(0, 1, 2, 0, 2, 0), 1, listOf(0, 1, 2, 0, 3, 0)),
        "Eaug" to GuitarChordDiagram(listOf(0, 3, 2, 1, 1, 0), 1, listOf(0, 4, 3, 1, 2, 0)),

        // F Chords
        "F" to GuitarChordDiagram(listOf(1, 3, 3, 2, 1, 1), 1, listOf(1, 3, 4, 2, 1, 1)),
        "Fm" to GuitarChordDiagram(listOf(1, 3, 3, 1, 1, 1), 1, listOf(1, 3, 4, 1, 1, 1)),
        "F7" to GuitarChordDiagram(listOf(1, 3, 1, 2, 1, 1), 1, listOf(1, 3, 1, 2, 1, 1)),
        "Fm7" to GuitarChordDiagram(listOf(1, 3, 1, 1, 1, 1), 1, listOf(1, 3, 1, 1, 1, 1)),
        "Fmaj7" to GuitarChordDiagram(listOf(-1, -1, 3, 2, 1, 0), 1, listOf(0, 0, 3, 2, 1, 0)),
        "Fsus2" to GuitarChordDiagram(listOf(-1, 3, 3, 0, 1, 1), 1, listOf(0, 3, 4, 0, 1, 1)),
        "Fsus4" to GuitarChordDiagram(listOf(1, 3, 3, 3, 1, 1), 1, listOf(1, 2, 3, 4, 1, 1)),
        "Fadd9" to GuitarChordDiagram(listOf(1, 3, 3, 2, 1, 3), 1, listOf(1, 2, 3, 1, 1, 4)),
        "Fdim" to GuitarChordDiagram(listOf(-1, -1, 3, 1, 0, 1), 1, listOf(0, 0, 3, 1, 0, 2)),
        "Faug" to GuitarChordDiagram(listOf(-1, -1, 3, 2, 2, 1), 1, listOf(0, 0, 3, 2, 2, 1)),

        // G Chords
        "G" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 3), 1, listOf(3, 2, 0, 0, 0, 4)),
        "Gm" to GuitarChordDiagram(listOf(3, 5, 5, 3, 3, 3), 3, listOf(1, 3, 4, 1, 1, 1)),
        "G7" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 1), 1, listOf(3, 2, 0, 0, 0, 1)),
        "Gm7" to GuitarChordDiagram(listOf(3, 5, 3, 3, 3, 3), 3, listOf(1, 3, 1, 1, 1, 1)),
        "Gmaj7" to GuitarChordDiagram(listOf(3, 2, 0, 0, 0, 2), 1, listOf(3, 2, 0, 0, 0, 1)),
        "Gsus2" to GuitarChordDiagram(listOf(3, 0, 0, 2, 0, 3), 1, listOf(2, 0, 0, 1, 0, 3)),
        "Gsus4" to GuitarChordDiagram(listOf(3, 3, 0, 0, 1, 3), 1, listOf(3, 4, 0, 0, 1, 2)),
        "Gadd9" to GuitarChordDiagram(listOf(3, 2, 0, 2, 0, 3), 1, listOf(2, 1, 0, 3, 0, 4)),
        "Gdim" to GuitarChordDiagram(listOf(3, -1, 2, 3, 2, -1), 1, listOf(2, 0, 1, 3, 1, 0)),
        "Gaug" to GuitarChordDiagram(listOf(3, 2, 1, 0, 0, 3), 1, listOf(3, 2, 1, 0, 0, 4)),

        // A Chords
        "A" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 2, 0), 1, listOf(0, 0, 1, 2, 3, 0)),
        "Am" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 1, 0), 1, listOf(0, 0, 2, 3, 1, 0)),
        "A7" to GuitarChordDiagram(listOf(-1, 0, 2, 0, 2, 0), 1, listOf(0, 0, 2, 0, 3, 0)),
        "Am7" to GuitarChordDiagram(listOf(-1, 0, 2, 0, 1, 0), 1, listOf(0, 0, 2, 0, 1, 0)),
        "Amaj7" to GuitarChordDiagram(listOf(-1, 0, 2, 1, 2, 0), 1, listOf(0, 0, 2, 1, 3, 0)),
        "Asus2" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 0, 0), 1, listOf(0, 0, 1, 2, 0, 0)),
        "Asus4" to GuitarChordDiagram(listOf(-1, 0, 2, 2, 3, 0), 1, listOf(0, 0, 1, 2, 3, 0)),
        "Aadd9" to GuitarChordDiagram(listOf(-1, 0, 2, 4, 2, 0), 1, listOf(0, 0, 1, 3, 2, 0)),
        "Adim" to GuitarChordDiagram(listOf(-1, 0, 1, 2, 1, -1), 1, listOf(0, 0, 1, 3, 2, 0)),
        "Aaug" to GuitarChordDiagram(listOf(-1, 0, 3, 2, 2, 1), 1, listOf(0, 0, 4, 2, 3, 1)),

        // B Chords
        "B" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 4, 2), 2, listOf(0, 1, 2, 3, 4, 1)),
        "Bm" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 3, 2), 2, listOf(0, 1, 3, 4, 2, 1)),
        "B7" to GuitarChordDiagram(listOf(-1, 2, 1, 2, 0, 2), 1, listOf(0, 2, 1, 3, 0, 4)),
        "Bm7" to GuitarChordDiagram(listOf(-1, 2, 0, 2, 0, 2), 1, listOf(0, 2, 0, 3, 0, 4)),
        "Bmaj7" to GuitarChordDiagram(listOf(-1, 2, 4, 3, 4, 2), 2, listOf(0, 1, 3, 2, 4, 1)),
        "Bsus2" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 2, 2), 2, listOf(0, 1, 3, 4, 1, 1)),
        "Bsus4" to GuitarChordDiagram(listOf(-1, 2, 4, 4, 5, 2), 2, listOf(0, 1, 2, 3, 4, 1)),
        "Bdim" to GuitarChordDiagram(listOf(-1, 2, 3, 2, 3, -1), 1, listOf(0, 1, 3, 2, 4, 0))
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
        val is7 = chord.suffix.contains("7") && !chord.suffix.contains("maj")

        val fretOffset = (pitch - Note.E.pitchClass).mod(12)
        val baseFret = if (fretOffset == 0) 1 else fretOffset

        val frets = if (isMinor && is7) {
            if (fretOffset == 0) listOf(0, 2, 0, 0, 0, 0)
            else listOf(baseFret, baseFret + 2, baseFret, baseFret, baseFret, baseFret)
        } else if (isMinor) {
            if (fretOffset == 0) listOf(0, 2, 2, 0, 0, 0)
            else listOf(baseFret, baseFret + 2, baseFret + 2, baseFret, baseFret, baseFret)
        } else if (is7) {
            if (fretOffset == 0) listOf(0, 2, 0, 1, 0, 0)
            else listOf(baseFret, baseFret + 2, baseFret, baseFret + 1, baseFret, baseFret)
        } else {
            if (fretOffset == 0) listOf(0, 2, 2, 1, 0, 0)
            else listOf(baseFret, baseFret + 2, baseFret + 2, baseFret + 1, baseFret, baseFret)
        }

        return GuitarChordDiagram(frets, baseFret)
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
