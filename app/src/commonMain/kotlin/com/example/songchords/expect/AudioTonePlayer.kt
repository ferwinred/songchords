package com.example.songchords

/**
 * Platform audio tone player abstraction for generating audio sine waves across platforms.
 */
expect class AudioTonePlayer() {
    fun playTone(frequencyHz: Double, durationMs: Long = 3000L)
    fun stopTone()
}
