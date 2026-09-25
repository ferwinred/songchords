package com.example.songchords

import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song

/**
 * Platform abstraction for generating PDF song sheets.
 */
expect class PdfExporter() {
    fun exportPdf(
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem = NotationSystem.STANDARD
    ): String?
}
