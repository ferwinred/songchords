package com.example.songchords

import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PlatformAbstractionsTest {

    @Test
    fun testGetPlatformName() {
        val platform = getPlatformName()
        assertEquals("Android", platform)
    }

    @Test
    fun testAudioTonePlayer() {
        val player = AudioTonePlayer()
        assertNotNull(player)
        player.playTone(440.0, 100L)
        player.stopTone()
    }

    @Test
    fun testPdfExporter() {
        val exporter = PdfExporter()
        assertNotNull(exporter)
        val song = Song(
            id = "test_1",
            title = "Test Song",
            artist = "Test Artist",
            originalKey = "C",
            content = "C G\nHello World"
        )
        val parsedSong = ParsedSong(song = song, lines = emptyList())
        val result = exporter.exportPdf(song, parsedSong, NotationSystem.STANDARD)
        assertEquals("Test_Song.pdf", result)
    }
}
