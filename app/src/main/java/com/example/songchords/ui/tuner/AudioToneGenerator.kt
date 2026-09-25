package com.example.songchords.ui.tuner

import com.example.songchords.AudioTonePlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioToneGenerator {
    private val player = AudioTonePlayer()

    var isPlaying: Boolean = false
        private set

    fun playTone(
        scope: CoroutineScope,
        frequencyHz: Double,
        durationMs: Long = 3000L,
        onComplete: () -> Unit = {}
    ) {
        stopTone()
        isPlaying = true
        player.playTone(frequencyHz, durationMs)
        scope.launch(Dispatchers.Main) {
            onComplete()
        }
    }

    fun stopTone() {
        player.stopTone()
        isPlaying = false
    }
}
