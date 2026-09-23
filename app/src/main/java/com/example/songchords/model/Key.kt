package com.example.songchords.model

/**
 * Represents a musical key (e.g. C Major, A Minor, F# Minor, Bb Major).
 */
data class Key(
    val root: Note,
    val isMinor: Boolean = false,
    val preferFlats: Boolean = calculatePreferFlats(root, isMinor)
) {
    fun name(notationSystem: NotationSystem = NotationSystem.STANDARD): String {
        val rootStr = root.toNotationName(notationSystem, preferFlats)
        return if (isMinor) "${rootStr}m" else rootStr
    }

    fun transpose(semitones: Int): Key {
        val newRoot = root.transpose(semitones)
        return Key(newRoot, isMinor)
    }

    companion object {
        fun parse(keyStr: String): Key? {
            val trimmed = keyStr.trim()
            if (trimmed.isEmpty()) return null

            var cleanStr = trimmed
            var isMinor = false

            if (cleanStr.endsWith("minor", ignoreCase = true)) {
                cleanStr = cleanStr.substring(0, cleanStr.length - 5).trim()
                isMinor = true
            } else if (cleanStr.endsWith("min", ignoreCase = true)) {
                cleanStr = cleanStr.substring(0, cleanStr.length - 3).trim()
                isMinor = true
            } else if (cleanStr.endsWith("m", ignoreCase = true) && !cleanStr.endsWith("dim", ignoreCase = true)) {
                cleanStr = cleanStr.substring(0, cleanStr.length - 1).trim()
                isMinor = true
            }

            val note = Note.fromName(cleanStr) ?: return null
            return Key(note, isMinor)
        }

        private fun calculatePreferFlats(root: Note, isMinor: Boolean): Boolean {
            return if (!isMinor) {
                root in listOf(Note.F, Note.A_SHARP, Note.D_SHARP, Note.G_SHARP, Note.C_SHARP, Note.F_SHARP)
            } else {
                root in listOf(Note.D, Note.G, Note.C, Note.F, Note.A_SHARP, Note.D_SHARP)
            }
        }
    }
}
