package com.example.songchords

import com.example.songchords.ui.tuner.InstrumentType
import com.example.songchords.ui.tuner.TunerEngine
import com.example.songchords.ui.tuner.TuningStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TunerEngineTest {

    @Test
    fun testGuitar6StringsLookup() {
        val strings = TunerEngine.getStringsForInstrument(InstrumentType.GUITAR_6)
        assertEquals(6, strings.size)

        assertEquals("E2", strings[0].noteName)
        assertEquals(82.41, strings[0].frequencyHz, 0.01)

        assertEquals("A2", strings[1].noteName)
        assertEquals(110.00, strings[1].frequencyHz, 0.01)

        assertEquals("D3", strings[2].noteName)
        assertEquals(146.83, strings[2].frequencyHz, 0.01)

        assertEquals("G3", strings[3].noteName)
        assertEquals(196.00, strings[3].frequencyHz, 0.01)

        assertEquals("B3", strings[4].noteName)
        assertEquals(246.94, strings[4].frequencyHz, 0.01)

        assertEquals("E4", strings[5].noteName)
        assertEquals(329.63, strings[5].frequencyHz, 0.01)
    }

    @Test
    fun testBass4StringsLookup() {
        val strings = TunerEngine.getStringsForInstrument(InstrumentType.BASS_4)
        assertEquals(4, strings.size)

        assertEquals("E1", strings[0].noteName)
        assertEquals(41.20, strings[0].frequencyHz, 0.01)

        assertEquals("A1", strings[1].noteName)
        assertEquals(55.00, strings[1].frequencyHz, 0.01)

        assertEquals("D2", strings[2].noteName)
        assertEquals(73.42, strings[2].frequencyHz, 0.01)

        assertEquals("G2", strings[3].noteName)
        assertEquals(98.00, strings[3].frequencyHz, 0.01)
    }

    @Test
    fun testBass5StringsLookup() {
        val strings = TunerEngine.getStringsForInstrument(InstrumentType.BASS_5)
        assertEquals(5, strings.size)

        assertEquals("B0", strings[0].noteName)
        assertEquals(30.87, strings[0].frequencyHz, 0.01)

        assertEquals("E1", strings[1].noteName)
        assertEquals(41.20, strings[1].frequencyHz, 0.01)

        assertEquals("A1", strings[2].noteName)
        assertEquals(55.00, strings[2].frequencyHz, 0.01)

        assertEquals("D2", strings[3].noteName)
        assertEquals(73.42, strings[3].frequencyHz, 0.01)

        assertEquals("G2", strings[4].noteName)
        assertEquals(98.00, strings[4].frequencyHz, 0.01)
    }

    @Test
    fun testCalculateCentsOffsetInTune() {
        val target = 440.0
        val actual = 440.0
        val cents = TunerEngine.calculateCentsOffset(actual, target)
        assertEquals(0.0, cents, 0.001)

        val status = TunerEngine.getTuningStatus(cents)
        assertEquals(TuningStatus.IN_TUNE, status)
    }

    @Test
    fun testCalculateCentsOffsetFlat() {
        val target = 100.0
        val actual = 95.0
        val cents = TunerEngine.calculateCentsOffset(actual, target)
        assertTrue("Cents should be negative when actual pitch is lower", cents < 0)

        val status = TunerEngine.getTuningStatus(cents)
        assertEquals(TuningStatus.FLAT, status)
    }

    @Test
    fun testCalculateCentsOffsetSharp() {
        val target = 100.0
        val actual = 105.0
        val cents = TunerEngine.calculateCentsOffset(actual, target)
        assertTrue("Cents should be positive when actual pitch is higher", cents > 0)

        val status = TunerEngine.getTuningStatus(cents)
        assertEquals(TuningStatus.SHARP, status)
    }

    @Test
    fun testOctaveCentsOffset() {
        val target = 100.0
        val octaveUp = 200.0
        val centsUp = TunerEngine.calculateCentsOffset(octaveUp, target)
        assertEquals(1200.0, centsUp, 0.01)

        val octaveDown = 50.0
        val centsDown = TunerEngine.calculateCentsOffset(octaveDown, target)
        assertEquals(-1200.0, centsDown, 0.01)
    }

    @Test
    fun testFrequencyFromCents() {
        val target = 440.0
        assertEquals(440.0, TunerEngine.frequencyFromCents(target, 0.0), 0.001)
        assertEquals(880.0, TunerEngine.frequencyFromCents(target, 1200.0), 0.001)
        assertEquals(220.0, TunerEngine.frequencyFromCents(target, -1200.0), 0.001)
    }
}
