package com.example.songchords

import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChordDiagramProviderTest {

    @Test
    fun testGuitarDiagramForCommonChords() {
        val cChord = Chord.parse("C")!!
        val cDiagram = ChordDiagramProvider.getGuitarDiagram(cChord)
        assertNotNull(cDiagram)
        assertEquals(1, cDiagram.baseFret)
        assertEquals(listOf(-1, 3, 2, 0, 1, 0), cDiagram.frets)

        val gChord = Chord.parse("G")!!
        val gDiagram = ChordDiagramProvider.getGuitarDiagram(gChord)
        assertNotNull(gDiagram)
        assertEquals(listOf(3, 2, 0, 0, 0, 3), gDiagram.frets)

        val emChord = Chord.parse("Em")!!
        val emDiagram = ChordDiagramProvider.getGuitarDiagram(emChord)
        assertNotNull(emDiagram)
        assertEquals(listOf(0, 2, 2, 0, 0, 0), emDiagram.frets)
    }

    @Test
    fun testGuitarDiagramFallbackTransposition() {
        // Test custom or unknown extension like C#m or F#m
        val fSharpMinor = Chord.parse("F#m")!!
        val diagram = ChordDiagramProvider.getGuitarDiagram(fSharpMinor)
        assertNotNull(diagram)
        assertEquals(2, diagram.baseFret)
    }

    @Test
    fun testPianoNotesForMajorAndMinorTriads() {
        val cMajor = Chord.parse("C")!!
        val cPiano = ChordDiagramProvider.getPianoNotes(cMajor)
        assertEquals(Note.C.pitchClass, cPiano.rootPitchClass)
        // C major = C(0), E(4), G(7)
        assertEquals(listOf(0, 4, 7), cPiano.pitchClasses)

        val aMinor = Chord.parse("Am")!!
        val aPiano = ChordDiagramProvider.getPianoNotes(aMinor)
        assertEquals(Note.A.pitchClass, aPiano.rootPitchClass)
        // A minor = A(9), C(0), E(4)
        assertEquals(listOf(0, 4, 9), aPiano.pitchClasses)
    }

    @Test
    fun testPianoNotesForSlashAnd7thChords() {
        val gSeventh = Chord.parse("G7")!!
        val g7Piano = ChordDiagramProvider.getPianoNotes(gSeventh)
        // G7 = G(7), B(11), D(2), F(5)
        assertTrue(g7Piano.pitchClasses.contains(Note.G.pitchClass))
        assertTrue(g7Piano.pitchClasses.contains(Note.B.pitchClass))
        assertTrue(g7Piano.pitchClasses.contains(Note.D.pitchClass))
        assertTrue(g7Piano.pitchClasses.contains(Note.F.pitchClass))

        val fSharpSlashA = Chord.parse("F#/A#")!!
        val fSharpPiano = ChordDiagramProvider.getPianoNotes(fSharpSlashA)
        assertEquals(Note.F_SHARP.pitchClass, fSharpPiano.rootPitchClass)
        assertEquals(Note.A_SHARP.pitchClass, fSharpPiano.bassPitchClass)
        assertTrue(fSharpPiano.pitchClasses.contains(Note.A_SHARP.pitchClass))
    }
}
