package com.example.songchords

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sin

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class AudioTonePlayer actual constructor() {
    private var audioTrack: AudioTrack? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    @Suppress("ACTUAL_WITHOUT_EXPECT")
    actual fun playTone(frequencyHz: Double, durationMs: Long) {
        stopTone()
        playJob = scope.launch {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val generatedSnd = ByteArray(2 * numSamples)

            for (i in 0 until numSamples) {
                val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz)
                val sample = (sin(angle) * 32767).toInt().coerceIn(-32768, 32767).toShort()
                generatedSnd[2 * i] = (sample.toInt() and 0x00ff).toByte()
                generatedSnd[2 * i + 1] = ((sample.toInt() and 0xff00) shr 8).toByte()
            }

            try {
                val bufferSize = generatedSnd.size
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack = track
                track.write(generatedSnd, 0, bufferSize)
                track.play()

                Thread.sleep(durationMs)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stopInternal()
            }
        }
    }

    @Suppress("ACTUAL_WITHOUT_EXPECT")
    actual fun stopTone() {
        playJob?.cancel()
        playJob = null
        stopInternal()
    }

    private fun stopInternal() {
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioTrack = null
        }
    }
}
