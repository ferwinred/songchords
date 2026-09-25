package com.example.songchords

import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class PdfExporter actual constructor() {
    @Suppress("ACTUAL_WITHOUT_EXPECT")
    actual fun exportPdf(
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem
    ): String? {
        val sanitizedTitle = song.title.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "$sanitizedTitle.pdf"
    }
}
