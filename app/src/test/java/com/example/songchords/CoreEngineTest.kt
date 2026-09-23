package com.example.songchords

import com.example.songchords.engine.ChordParser
import com.example.songchords.engine.NotationConverter
import com.example.songchords.engine.TranspositionEngine
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordLyricsLine
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.Note
import com.example.songchords.repository.SampleSongs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreEngineTest {

    @Test
    fun testChordParsing() {
        // Simple chord parsing
        val chordG = Chord.parse("G")
        assertNotNull(chordG)
        assertEquals(Note.G, chordG!!.root)
        assertEquals("", chordG.suffix)

        val chordCadd2 = Chord.parse("Cadd2")
        assertNotNull(chordCadd2)
        assertEquals(Note.C, chordCadd2!!.root)
        assertEquals("add2", chordCadd2.suffix)

        val chordSlash = Chord.parse("F#/A#")
        assertNotNull(chordSlash)
        assertEquals(Note.F_SHARP, chordSlash!!.root)
        assertEquals("", chordSlash.suffix)
        assertEquals(Note.A_SHARP, chordSlash.bass)

        // Solfege chord parsing
        val chordSol = Chord.parse("Solm7")
        assertNotNull(chordSol)
        assertEquals(Note.G, chordSol!!.root)
        assertEquals("m7", chordSol.suffix)
    }

    @Test
    fun testLineParsing() {
        val lineStr = "[G] Tu poeta, tu [Cadd2]canción"
        val parsedLine = ChordParser.parseLine(lineStr)

        assertTrue(parsedLine is ChordLyricsLine.ChordLyrics)
        val chordLine = parsedLine as ChordLyricsLine.ChordLyrics

        assertEquals("Tu poeta, tu canción", chordLine.plainText)
        assertEquals(2, chordLine.chordPositions.size)

        assertEquals(Note.G, chordLine.chordPositions[0].chord.root)
        assertEquals(0, chordLine.chordPositions[0].charIndex)

        assertEquals(Note.C, chordLine.chordPositions[1].chord.root)
        assertEquals("add2", chordLine.chordPositions[1].chord.suffix)
        assertEquals(13, chordLine.chordPositions[1].charIndex)
    }

    @Test
    fun testEmbeddedWordChordParsing() {
        val embeddedLine = ChordParser.parseLine("Se[A]ñor con[D]templar es[E]trellas")
        assertTrue(embeddedLine is ChordLyricsLine.ChordLyrics)
        val chordLine = embeddedLine as ChordLyricsLine.ChordLyrics

        assertEquals("Señor contemplar estrellas", chordLine.plainText)
        assertEquals(3, chordLine.chordPositions.size)

        assertEquals(Note.A, chordLine.chordPositions[0].chord.root)
        assertEquals(2, chordLine.chordPositions[0].charIndex)

        assertEquals(Note.D, chordLine.chordPositions[1].chord.root)
        assertEquals(9, chordLine.chordPositions[1].charIndex)

        assertEquals(Note.E, chordLine.chordPositions[2].chord.root)
        assertEquals(19, chordLine.chordPositions[2].charIndex)
    }

    @Test
    fun testCuanGrandeEsElLineParsing() {
        val lineStr = "Se[A]ñor mi Dios, al con[D]templar los cielos"
        val parsedLine = ChordParser.parseLine(lineStr)

        assertTrue(parsedLine is ChordLyricsLine.ChordLyrics)
        val chordLine = parsedLine as ChordLyricsLine.ChordLyrics

        assertEquals("Señor mi Dios, al contemplar los cielos", chordLine.plainText)
        assertEquals(2, chordLine.chordPositions.size)

        assertEquals(Note.A, chordLine.chordPositions[0].chord.root)
        assertEquals(2, chordLine.chordPositions[0].charIndex)

        assertEquals(Note.D, chordLine.chordPositions[1].chord.root)
        assertEquals(21, chordLine.chordPositions[1].charIndex)
    }

    @Test
    fun testSectionHeaderParsing() {
        val verseHeader = ChordParser.parseLine("[Verse 1]")
        assertTrue(verseHeader is ChordLyricsLine.SectionHeader)
        assertEquals("Verse 1", (verseHeader as ChordLyricsLine.SectionHeader).title)

        val chorusHeader = ChordParser.parseLine("[Chorus]")
        assertTrue(chorusHeader is ChordLyricsLine.SectionHeader)
        assertEquals("Chorus", (chorusHeader as ChordLyricsLine.SectionHeader).title)
    }

    @Test
    fun testTransposition() {
        val chordG = Chord.parse("G")!!

        // Transpose G up 2 semitones (+1 full tone) -> A
        val transposedUp2 = TranspositionEngine.transpose(chordG, 2)
        assertEquals("A", transposedUp2.formatted(NotationSystem.STANDARD))

        // Transpose G down 2 semitones (-1 full tone) -> F
        val transposedDown2 = TranspositionEngine.transpose(chordG, -2)
        assertEquals("F", transposedDown2.formatted(NotationSystem.STANDARD))

        // Transpose G up 1 semitone (+1/2 tone) -> G#
        val transposedUp1 = TranspositionEngine.transpose(chordG, 1)
        assertEquals("G#", transposedUp1.formatted(NotationSystem.STANDARD))

        // Transpose G down 1 semitone (-1/2 tone) -> F#
        val transposedDown1 = TranspositionEngine.transpose(chordG, -1)
        assertEquals("F#", transposedDown1.formatted(NotationSystem.STANDARD))

        // Test TranspositionEngine tone helpers
        val fullToneUp = TranspositionEngine.transposeFullTone(chordG, 1)
        assertEquals("A", fullToneUp.formatted(NotationSystem.STANDARD))

        val fullToneDown = TranspositionEngine.transposeFullTone(chordG, -1)
        assertEquals("F", fullToneDown.formatted(NotationSystem.STANDARD))

        val halfToneUp = TranspositionEngine.transposeHalfTone(chordG, 1)
        assertEquals("G#", halfToneUp.formatted(NotationSystem.STANDARD))

        val halfToneDown = TranspositionEngine.transposeHalfTone(chordG, -1)
        assertEquals("F#", halfToneDown.formatted(NotationSystem.STANDARD))

        // Transpose slash chord F#/A# up 1 semitone -> G/B
        val slashChord = Chord.parse("F#/A#")!!
        val slashTransposed = TranspositionEngine.transpose(slashChord, 1)
        assertEquals("G/B", slashTransposed.formatted(NotationSystem.STANDARD))
    }

    @Test
    fun testNotationConversion() {
        val chordCadd2 = Chord.parse("Cadd2")!!
        val solfegeStr = NotationConverter.formatChord(chordCadd2, NotationSystem.SOLFEGE)
        assertEquals("Doadd2", solfegeStr)

        val chordEm = Chord.parse("Em")!!
        val solfegeEm = NotationConverter.formatChord(chordEm, NotationSystem.SOLFEGE)
        assertEquals("Mim", solfegeEm)

        val rawText = "[G] Tu poeta, tu [Cadd2]canción"
        val solfegeText = NotationConverter.convertText(rawText, NotationSystem.SOLFEGE)
        assertEquals("[Sol]Tu poeta, tu [Doadd2]canción", solfegeText)
    }

    @Test
    fun testSampleSongParsingAndTransposition() {
        val tuPoeta = SampleSongs.TU_POETA
        val parsedSong = ChordParser.parseSong(tuPoeta)

        assertEquals("G", parsedSong.currentKey?.name())

        // Transpose +2 semitones (G -> A)
        val transposedSong = parsedSong.transpose(2)
        assertEquals("A", transposedSong.currentKey?.name())

        val transposedText = transposedSong.toChordTaggedText()
        assertTrue(transposedText.contains("[A]"))
        assertTrue(transposedText.contains("[Dadd2]"))
        assertTrue(transposedText.contains("[E]"))
        assertTrue(transposedText.contains("[F#m]"))
    }
}
