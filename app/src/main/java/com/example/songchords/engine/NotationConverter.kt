package com.example.songchords.engine

import com.example.songchords.model.Chord
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song

object NotationConverter {

    fun formatChord(chord: Chord, targetNotation: NotationSystem, preferFlats: Boolean = false): String {
        return chord.formatted(targetNotation, preferFlats)
    }

    fun convertParsedSong(parsedSong: ParsedSong, targetNotation: NotationSystem): ParsedSong {
        return parsedSong.withNotationSystem(targetNotation)
    }

    fun convertText(
        content: String,
        targetNotation: NotationSystem,
        preferFlats: Boolean = false
    ): String {
        val dummySong = Song(
            title = "", artist = "", originalKey = "C", content = content
        )
        val parsed = ChordParser.parseSong(dummySong, targetNotation)
        return parsed.toChordTaggedText()
    }
}
