package com.example.songchords

import com.example.songchords.engine.ChordParser
import com.example.songchords.model.NotationSystem
import com.example.songchords.repository.SampleSongs
import com.example.songchords.utils.SongExportUtils
import org.junit.Assert.assertTrue
import org.junit.Test

class SongExportUtilsTest {

    @Test
    fun testGenerateFormattedTextStandard() {
        val song = SampleSongs.TU_POETA
        val parsedSong = ChordParser.parseSong(song, NotationSystem.STANDARD)
        val exportText = SongExportUtils.generateFormattedText(
            song = song,
            parsedSong = parsedSong,
            notationSystem = NotationSystem.STANDARD
        )

        assertTrue(exportText.contains("Tu Poeta"))
        assertTrue(exportText.contains("Artist: Alex Campos"))
        assertTrue(exportText.contains("Key: G"))
        assertTrue(exportText.contains("Tempo: 72 BPM"))
        assertTrue(exportText.contains("Time Signature: 4/4"))
        assertTrue(exportText.contains("[G]"))
        assertTrue(exportText.contains("[Cadd2]"))
    }

    @Test
    fun testGenerateFormattedTextTransposedAndSolfege() {
        val song = SampleSongs.TU_POETA
        val parsedSong = ChordParser.parseSong(song, NotationSystem.SOLFEGE)
            .transpose(2) // G -> A

        val exportText = SongExportUtils.generateFormattedText(
            song = song,
            parsedSong = parsedSong,
            notationSystem = NotationSystem.SOLFEGE
        )

        assertTrue(exportText.contains("Tu Poeta"))
        assertTrue(exportText.contains("Key: La")) // A in Solfege is La
        assertTrue(exportText.contains("[La]"))
        assertTrue(exportText.contains("[Fa#m]"))
        assertTrue(exportText.contains("[Readd2]"))
    }
}
