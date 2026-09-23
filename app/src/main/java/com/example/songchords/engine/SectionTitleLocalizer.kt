package com.example.songchords.engine

import java.util.Locale

enum class SectionType(
    val englishName: String,
    val spanishName: String,
    val keywords: List<String>,
) {
    PRE_CHORUS(
        englishName = "Pre-Chorus",
        spanishName = "Pre-Coro",
        keywords = listOf("pre-chorus", "prechorus", "pre chorus", "pre-coro", "precoro", "pre coro"),
    ),
    CHORUS(
        englishName = "Chorus",
        spanishName = "Coro",
        keywords = listOf("chorus", "coro", "refrain"),
    ),
    VERSE(
        englishName = "Verse",
        spanishName = "Estrofa",
        keywords = listOf("verse", "estrofa", "verso"),
    ),
    BRIDGE(
        englishName = "Bridge",
        spanishName = "Puente",
        keywords = listOf("bridge", "puente"),
    ),
    OUTRO(
        englishName = "Outro",
        spanishName = "Final",
        keywords = listOf("outro", "final", "ending", "salida"),
    ),
    INTRO(
        englishName = "Intro",
        spanishName = "Introducción",
        keywords = listOf("intro", "introducción", "introduccion", "introduction"),
    ),
    SOLO(
        englishName = "Solo",
        spanishName = "Solo",
        keywords = listOf("solo"),
    ),
    INTERLUDE(
        englishName = "Interlude",
        spanishName = "Interludio",
        keywords = listOf("interlude", "interludio"),
    );
}

object SectionTitleLocalizer {

    fun localize(title: String, isSpanish: Boolean): String {
        val targetLang = if (isSpanish) "es" else "en"
        return localize(title, targetLang)
    }

    fun localize(title: String, locale: Locale = Locale.getDefault()): String {
        val lang = locale.language.lowercase()
        return localize(title, if (lang.startsWith("es")) "es" else "en")
    }

    fun localize(title: String, languageCode: String): String {
        val cleanTitle = title.trim()
            .removeSuffix(":")
            .trim()
            .removeSurrounding("[", "]")
            .removeSuffix(":")
            .trim()

        if (cleanTitle.isEmpty()) return title

        val lowerTitle = cleanTitle.lowercase()

        for (sectionType in SectionType.entries) {
            for (keyword in sectionType.keywords) {
                if (lowerTitle == keyword) {
                    return if (languageCode == "es") sectionType.spanishName else sectionType.englishName
                }
                if (lowerTitle.startsWith(keyword)) {
                    val isBoundary = (lowerTitle.length == keyword.length) || run {
                        val nextChar = lowerTitle[keyword.length]
                        nextChar.isWhitespace() || (nextChar == '-') || (nextChar == '_') || (nextChar == ':') || (nextChar == '.') || nextChar.isDigit()
                    }

                    if (isBoundary) {
                        val rawRemainder = cleanTitle.substring(keyword.length)
                        val strippedRemainder = rawRemainder
                            .trim()
                            .removePrefix("-")
                            .removePrefix("_")
                            .removePrefix(":")
                            .removePrefix(".")
                            .trim()

                        val baseName = if (languageCode == "es") sectionType.spanishName else sectionType.englishName
                        val normalizedSubLabel = normalizeSubLabel(strippedRemainder)

                        return if (normalizedSubLabel.isNotEmpty()) "$baseName $normalizedSubLabel" else baseName
                    }
                }
            }
        }

        return cleanTitle
    }

    private fun normalizeSubLabel(subLabel: String): String {
        val trimmed = subLabel.trim()
        return when (trimmed.uppercase()) {
            "I" -> "1"
            "II" -> "2"
            "III" -> "3"
            "IV" -> "4"
            "V" -> "5"
            "VI" -> "6"
            "VII" -> "7"
            "VIII" -> "8"
            "IX" -> "9"
            "X" -> "10"
            else -> trimmed
        }
    }
}
