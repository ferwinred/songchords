package com.example.songchords.engine

import com.example.songchords.model.Chord
import com.example.songchords.model.ChordLyricsLine
import com.example.songchords.model.ChordPosition
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song

object ChordParser {

    private val BRACKET_REGEX = Regex("\\[([^]]+)\\]")
    private val SECTION_HEADER_REGEX = Regex("(?i)^\\[?(intro|introducción|introduccion|verse|verso|estrofa|chorus|coro|refrain|bridge|puente|outro|final|ending|salida|pre-chorus|prechorus|pre-coro|precoro|solo|interlude|interludio|tab|coda)(\\s*[-:_]?\\s*([0-9]+|[a-z]+|[ivxlcdm]+))?\\]?:?\\s*$")

    fun parseSong(
        song: Song,
        notationSystem: NotationSystem = NotationSystem.STANDARD,
    ): ParsedSong {
        val lines = song.content.lines().map { parseLine(it) }
        return ParsedSong(
            song = song,
            lines = lines,
            notationSystem = notationSystem,
        )
    }

    fun parseLine(lineText: String): ChordLyricsLine {
        val trimmed = lineText.trim()
        if (trimmed.isEmpty()) {
            return ChordLyricsLine.EmptyLine
        }

        if (SECTION_HEADER_REGEX.matches(trimmed)) {
            val title = trimmed.removeSurrounding("[", "]").removeSuffix(":").trim()
            return ChordLyricsLine.SectionHeader(title)
        }

        val matches = BRACKET_REGEX.findAll(trimmed).toList()
        if (matches.isEmpty()) {
            return ChordLyricsLine.ChordLyrics(
                plainText = trimmed,
                chordPositions = emptyList()
            )
        }

        val plainTextBuilder = StringBuilder()
        val chordPositions = mutableListOf<ChordPosition>()
        var lastIndex = 0

        for (match in matches) {
            val chordName = match.groupValues[1]
            val chord = Chord.parse(chordName)

            if (chord != null) {
                val textBefore = trimmed.substring(lastIndex, match.range.first)
                plainTextBuilder.append(textBefore)

                val charIndex = plainTextBuilder.length
                chordPositions.add(ChordPosition(chord, charIndex))

                var nextIndex = match.range.last + 1
                if (nextIndex < trimmed.length && trimmed[nextIndex] == ' ') {
                    if (match.range.first == 0 || plainTextBuilder.isEmpty() || plainTextBuilder.endsWith(' ')) {
                        nextIndex++
                    }
                }
                lastIndex = nextIndex
            } else {
                val textBeforeAndMatch = trimmed.substring(lastIndex, match.range.last + 1)
                plainTextBuilder.append(textBeforeAndMatch)
                lastIndex = match.range.last + 1
            }
        }

        if (lastIndex < trimmed.length) {
            plainTextBuilder.append(trimmed.substring(lastIndex))
        }

        val finalPlainText = plainTextBuilder.toString()

        return ChordLyricsLine.ChordLyrics(
            plainText = finalPlainText,
            chordPositions = chordPositions
        )
    }
}
