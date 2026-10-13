package com.example.songchords.tuner

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.math.sqrt

class MicrophonePitchDetector(
    private val sampleRate: Int = 44100,
) {
    @Volatile
    private var isListening = false
    private var recordingThread: Thread? = null

    @SuppressLint("MissingPermission")
    fun startListening(onPitchDetected: (Float) -> Unit) {
        if (isListening) {
            stopListening()
        }

        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding)
        val bufferSize = maxOf(minBufferSize, 4096)

        var audioRecord: AudioRecord? = null
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioEncoding,
                bufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize")
                return
            }

            audioRecord.startRecording()
            isListening = true

            recordingThread = Thread {
                val buffer = ShortArray(2048)
                while (isListening && !Thread.currentThread().isInterrupted) {
                    val readCount = audioRecord.read(buffer, 0, buffer.size)
                    if (readCount > 0) {
                        val pitch = detectPitch(buffer, sampleRate, readCount)
                        if (pitch > 0f) {
                            onPitchDetected(pitch)
                        }
                    }
                }
                try {
                    if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        audioRecord.stop()
                    }
                    audioRecord.release()
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping AudioRecord", e)
                }
            }.apply { start() }

        } catch (e: SecurityException) {
            Log.e(TAG, "RECORD_AUDIO permission missing or denied", e)
            isListening = false
        } catch (e: Exception) {
            Log.e(TAG, "Error starting microphone pitch detector", e)
            isListening = false
            audioRecord?.release()
        }
    }

    fun stopListening() {
        isListening = false
        recordingThread?.interrupt()
        recordingThread = null
    }

    companion object {
        private const val TAG = "MicrophonePitchDetector"

        /**
         * Autocorrelation pitch detection algorithm.
         * Calculates fundamental frequency f0 (in Hz) for PCM 16-bit audio data.
         *
         * @param pcmData ShortArray of 16-bit PCM audio samples.
         * @param sampleRate Sample rate in Hz (default 44100).
         * @param length Number of valid samples in pcmData to process (default pcmData.size).
         * @return Fundamental frequency f0 in Hz, or -1f if no clear pitch is detected.
         */
        fun detectPitch(
            pcmData: ShortArray,
            sampleRate: Int = 44100,
            length: Int = pcmData.size,
        ): Float {
            val validLen = length.coerceAtMost(pcmData.size)
            if (validLen <= 0) return -1f

            // 1. Calculate RMS to filter out silence / background noise
            var sumSquare = 0.0
            for (i in 0 until validLen) {
                val sample = pcmData[i].toDouble()
                sumSquare += sample * sample
            }
            val rms = sqrt(sumSquare / validLen)
            if (rms < 150.0) {
                return -1f
            }

            // 2. Define lag search range for musical pitch (30 Hz to 1200 Hz)
            val minFreq = 30.0
            val maxFreq = 1200.0

            val maxLag = (sampleRate / minFreq).toInt().coerceAtMost(validLen - 1)
            val minLag = (sampleRate / maxFreq).toInt().coerceAtLeast(1)

            if (minLag >= maxLag || maxLag >= validLen) {
                return -1f
            }

            // 3. Compute autocorrelation R(tau)
            val r = FloatArray(maxLag + 1)
            for (tau in 0..maxLag) {
                var sum = 0.0f
                val end = validLen - tau
                for (i in 0 until end) {
                    sum += pcmData[i].toFloat() * pcmData[i + tau].toFloat()
                }
                r[tau] = sum
            }

            val r0 = r[0]
            if (r0 <= 0f) return -1f

            // 4. Find first zero crossing or local minimum after tau = 0
            var tauStart = minLag
            while (tauStart < maxLag && r[tauStart] > r[tauStart - 1]) {
                tauStart++
            }
            while (tauStart < maxLag && r[tauStart] <= r[tauStart - 1]) {
                tauStart++
            }

            // 5. Search for peak lag with maximum autocorrelation
            var peakLag = -1
            var maxVal = 0f

            for (tau in tauStart..maxLag) {
                if (r[tau] > maxVal) {
                    maxVal = r[tau]
                    peakLag = tau
                }
            }

            if (peakLag <= 0 || peakLag < minLag || peakLag >= maxLag) {
                return -1f
            }

            // Check normalized autocorrelation confidence
            val confidence = maxVal / r0
            if (confidence < 0.25f) {
                return -1f
            }

            // 6. Parabolic interpolation around peakLag for precise frequency
            val y1 = r[peakLag - 1]
            val y2 = r[peakLag]
            val y3 = r[peakLag + 1]

            val denominator = y1 - 2 * y2 + y3
            val delta = if (denominator != 0f) {
                (y1 - y3) / (2 * denominator)
            } else {
                0f
            }

            val refinedLag = peakLag + delta
            if (refinedLag <= 0) return -1f

            val f0 = sampleRate.toFloat() / refinedLag
            return if (f0 in minFreq.toFloat()..maxFreq.toFloat()) f0 else -1f
        }
    }
}
