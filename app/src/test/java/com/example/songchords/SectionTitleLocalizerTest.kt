package com.example.songchords

import com.example.songchords.engine.SectionTitleLocalizer
import org.junit.Assert.assertEquals
import org.junit.Test

class SectionTitleLocalizerTest {

    @Test
    fun testChorusLocalization() {
        assertEquals("Coro", SectionTitleLocalizer.localize("Chorus", "es"))
        assertEquals("Chorus", SectionTitleLocalizer.localize("Chorus", "en"))

        assertEquals("Coro", SectionTitleLocalizer.localize("Coro", "es"))
        assertEquals("Chorus", SectionTitleLocalizer.localize("Coro", "en"))
    }

    @Test
    fun testVerseLocalization() {
        assertEquals("Estrofa", SectionTitleLocalizer.localize("Verse", "es"))
        assertEquals("Verse", SectionTitleLocalizer.localize("Verse", "en"))

        assertEquals("Estrofa", SectionTitleLocalizer.localize("Estrofa", "es"))
        assertEquals("Verse", SectionTitleLocalizer.localize("Estrofa", "en"))
    }

    @Test
    fun testBridgeLocalization() {
        assertEquals("Puente", SectionTitleLocalizer.localize("Bridge", "es"))
        assertEquals("Bridge", SectionTitleLocalizer.localize("Bridge", "en"))

        assertEquals("Puente", SectionTitleLocalizer.localize("Puente", "es"))
        assertEquals("Bridge", SectionTitleLocalizer.localize("Puente", "en"))
    }

    @Test
    fun testOutroLocalization() {
        assertEquals("Final", SectionTitleLocalizer.localize("Outro", "es"))
        assertEquals("Outro", SectionTitleLocalizer.localize("Outro", "en"))

        assertEquals("Final", SectionTitleLocalizer.localize("Final", "es"))
        assertEquals("Outro", SectionTitleLocalizer.localize("Final", "en"))
    }

    @Test
    fun testPreChorusLocalization() {
        assertEquals("Pre-Coro", SectionTitleLocalizer.localize("Pre-Chorus", "es"))
        assertEquals("Pre-Chorus", SectionTitleLocalizer.localize("Pre-Chorus", "en"))

        assertEquals("Pre-Coro", SectionTitleLocalizer.localize("Pre-Coro", "es"))
        assertEquals("Pre-Chorus", SectionTitleLocalizer.localize("Pre-Coro", "en"))
    }

    @Test
    fun testNumbersAndSubLabelsPreservation() {
        assertEquals("Estrofa 2", SectionTitleLocalizer.localize("Verse 2", "es"))
        assertEquals("Verse 2", SectionTitleLocalizer.localize("Verse 2", "en"))

        assertEquals("Estrofa 2", SectionTitleLocalizer.localize("ESTROFA II", "es"))
        assertEquals("Verse 2", SectionTitleLocalizer.localize("ESTROFA II", "en"))

        assertEquals("Pre-Coro 1", SectionTitleLocalizer.localize("[Pre-Coro 1]:", "es"))
        assertEquals("Pre-Chorus 1", SectionTitleLocalizer.localize("[Pre-Coro 1]:", "en"))

        assertEquals("Final 2", SectionTitleLocalizer.localize("Outro 2", "es"))
        assertEquals("Outro 2", SectionTitleLocalizer.localize("Outro 2", "en"))
    }
}
