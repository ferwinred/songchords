package com.example.songchords.model

/**
 * Represents a parsed musical chord.
 * e.g., root = C, suffix = "add2", bass = null -> "Cadd2"
 * e.g., root = F#, suffix = "", bass = A# -> "F#/A#"
 */
data class Chord(
    val root: Note,
    val suffix: String = "",
    val bass: Note? = null,
    val rawSymbol: String = ""
) {
    fun transpose(semitones: Int, preferFlats: Boolean = false): Chord {
        val newRoot = root.transpose(semitones)
        val newBass = bass?.transpose(semitones)
        return Chord(
            root = newRoot,
            suffix = suffix,
            bass = newBass,
            rawSymbol = rawSymbol
        )
    }

    fun formatted(notationSystem: NotationSystem = NotationSystem.STANDARD, preferFlats: Boolean = false): String {
        val rootStr = root.toNotationName(notationSystem, preferFlats)
        val bassStr = bass?.let { "/" + it.toNotationName(notationSystem, preferFlats) } ?: ""
        return "$rootStr$suffix$bassStr"
    }

    companion object {
        private val ALLOWED_SUFFIX_CHARS = Regex("^[a-zA-Z0-9#+\\-()/]*$")

        fun parse(symbol: String): Chord? {
            val trimmed = symbol.trim()
            if (trimmed.isEmpty()) return null

            // Disallow known section labels
            if (trimmed.matches(Regex("(?i)^(verse|chorus|bridge|intro|outro|solo|interlude|tab|ending|refrain|pre-chorus|coro|verso|puente|introducción).*"))) {
                return null
            }

            // Check for slash bass note e.g. "G/B", "F#/A#"
            val slashIndex = trimmed.indexOf('/')
            val mainPart: String
            val bassNote: Note?

            if (slashIndex != -1) {
                mainPart = trimmed.substring(0, slashIndex)
                val bassPart = trimmed.substring(slashIndex + 1)
                bassNote = Note.fromName(bassPart)
                if (bassNote == null) return null
            } else {
                mainPart = trimmed
                bassNote = null
            }

            val parseResult = Note.parsePrefix(mainPart) ?: return null
            val rootNote = parseResult.first
            val suffix = parseResult.second

            if (suffix.isNotEmpty()) {
                if (!suffix.matches(ALLOWED_SUFFIX_CHARS)) return null

                val cleanSuffix = suffix.lowercase()
                if (cleanSuffix.contains("ridge") || cleanSuffix.contains("tro") || cleanSuffix.contains("erse") || cleanSuffix.contains("coro")) {
                    return null
                }
            }

            return Chord(
                root = rootNote,
                suffix = suffix,
                bass = bassNote,
                rawSymbol = trimmed
            )
        }
    }
}
