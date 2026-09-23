package com.example.songchords.model

/**
 * Enum representing the 12 chromatic pitch classes in Western music.
 */
enum class Note(
    val pitchClass: Int,
    val standardSharp: String,
    val standardFlat: String,
    val solfegeSharp: String,
    val solfegeFlat: String
) {
    C(0, "C", "C", "Do", "Do"),
    C_SHARP(1, "C#", "Db", "Do#", "Reb"),
    D(2, "D", "D", "Re", "Re"),
    D_SHARP(3, "D#", "Eb", "Re#", "Mib"),
    E(4, "E", "E", "Mi", "Mi"),
    F(5, "F", "F", "Fa", "Fa"),
    F_SHARP(6, "F#", "Gb", "Fa#", "Solb"),
    G(7, "G", "G", "Sol", "Sol"),
    G_SHARP(8, "G#", "Ab", "Sol#", "Lab"),
    A(9, "A", "A", "La", "La"),
    A_SHARP(10, "A#", "Bb", "La#", "Sib"),
    B(11, "B", "B", "Si", "Si");

    fun toStandardName(preferFlats: Boolean = false): String =
        if (preferFlats) standardFlat else standardSharp

    fun toSolfegeName(preferFlats: Boolean = false): String =
        if (preferFlats) solfegeFlat else solfegeSharp

    fun toNotationName(notationSystem: NotationSystem, preferFlats: Boolean = false): String =
        when (notationSystem) {
            NotationSystem.STANDARD -> toStandardName(preferFlats)
            NotationSystem.SOLFEGE -> toSolfegeName(preferFlats)
        }

    fun transpose(semitones: Int): Note {
        val newPitch = (pitchClass + semitones).mod(12)
        return fromPitchClass(newPitch)
    }

    companion object {
        fun fromPitchClass(pitchClass: Int): Note {
            val normalized = pitchClass.mod(12)
            return entries.first { it.pitchClass == normalized }
        }

        /**
         * Parses a note root at the beginning of an input string.
         * Returns a Pair of (Note, remainingString), or null if no note matches.
         */
        fun parsePrefix(input: String): Pair<Note, String>? {
            val trimmed = input.trimStart()
            if (trimmed.isEmpty()) return null

            val candidates = mutableListOf<Pair<String, Note>>()
            for (note in entries) {
                candidates.add(note.standardSharp to note)
                candidates.add(note.standardFlat to note)
                candidates.add(note.solfegeSharp to note)
                candidates.add(note.solfegeFlat to note)
            }

            // Sort candidates by length descending to match longest candidate first
            // (e.g. "Sol#" before "Sol", "Do#" before "Do", "F#" before "F")
            val sorted = candidates
                .distinctBy { it.first.uppercase() }
                .sortedByDescending { it.first.length }

            for ((name, note) in sorted) {
                if (trimmed.startsWith(name, ignoreCase = true)) {
                    val remaining = trimmed.substring(name.length)
                    return note to remaining
                }
            }
            return null
        }

        fun fromName(name: String): Note? {
            val parseResult = parsePrefix(name) ?: return null
            if (parseResult.second.isEmpty()) {
                return parseResult.first
            }
            return null
        }
    }
}
