package com.example.songchords.tuner

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class MicrophonePitchDetectorTest {

    @Test
    fun testSilenceReturnsNoPitch() {
        val sampleRate = 44100
        val buffer = ShortArray(2048) { 0 } // Complete silence
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(-1f, detectedPitch, 0.0f)
    }

    @Test
    fun testLowNoiseReturnsNoPitch() {
        val sampleRate = 44100
        val buffer = ShortArray(2048) { (it % 10 - 5).toShort() } // Very low noise under threshold
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(-1f, detectedPitch, 0.0f)
    }

    @Test
    fun testEmptyBufferReturnsNoPitch() {
        val sampleRate = 44100
        val buffer = ShortArray(0)
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(-1f, detectedPitch, 0.0f)
    }

    @Test
    fun testConcertA440HzPitchDetection() {
        val sampleRate = 44100
        val targetFreq = 440.0
        val buffer = ShortArray(2048)
        for (i in buffer.indices) {
            val sample = (25000.0 * sin(2.0 * PI * targetFreq * i / sampleRate)).toInt().toShort()
            buffer[i] = sample
        }
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(440f, detectedPitch, 2.0f)
    }

    @Test
    fun testGuitarStringE2PitchDetection() {
        val sampleRate = 44100
        val targetFreq = 82.41
        val buffer = ShortArray(4096)
        for (i in buffer.indices) {
            val sample = (25000.0 * sin(2.0 * PI * targetFreq * i / sampleRate)).toInt().toShort()
            buffer[i] = sample
        }
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(82.41f, detectedPitch, 1.5f)
    }

    @Test
    fun testGuitarStringE4PitchDetection() {
        val sampleRate = 44100
        val targetFreq = 329.63
        val buffer = ShortArray(2048)
        for (i in buffer.indices) {
            val sample = (25000.0 * sin(2.0 * PI * targetFreq * i / sampleRate)).toInt().toShort()
            buffer[i] = sample
        }
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(329.63f, detectedPitch, 1.5f)
    }

    @Test
    fun testBassStringA1PitchDetection() {
        val sampleRate = 44100
        val targetFreq = 55.0
        val buffer = ShortArray(4096)
        for (i in buffer.indices) {
            val sample = (25000.0 * sin(2.0 * PI * targetFreq * i / sampleRate)).toInt().toShort()
            buffer[i] = sample
        }
        val detectedPitch = MicrophonePitchDetector.detectPitch(buffer, sampleRate)
        assertEquals(55.0f, detectedPitch, 1.5f)
    }
}
