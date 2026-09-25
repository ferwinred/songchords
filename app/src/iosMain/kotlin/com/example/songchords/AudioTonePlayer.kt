package com.example.songchords

actual class AudioTonePlayer actual constructor() {
    private var isPlayingInternal: Boolean = false

    actual fun playTone(frequencyHz: Double, durationMs: Long) {
        isPlayingInternal = true
    }

    actual fun stopTone() {
        isPlayingInternal = false
    }
}
