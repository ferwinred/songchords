# Project Plan

SongChords app - Implement real-time microphone pitch detection in TunerScreen using AudioRecord and Autocorrelation algorithm to detect instrument sound live.

## Project Brief

# SongChords - Project Brief

## Features
- **Real-Time Microphone Pitch Detection**: Captures live audio input via Android `AudioRecord` (PCM 16-bit, 44.1 kHz) and processes pitch frequency using an Autocorrelation algorithm.
- **Live Visual Tuner & Needle Feedback**: Evaluates detected pitch against target instrument strings (Guitar 6-string, Bass 4/5-string) to animate a gauge needle displaying cent offsets (-50 to +50 cents).
- **Fallback Reference Tones**: Offers manual reference tone audio playback for tuning alongside live microphone detection.
- **Runtime Audio Permissions**: Prompts and handles `RECORD_AUDIO` permissions directly within the tuner workflow.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) & Compose Material Adaptive library
- **Audio Processing**: Android `AudioRecord` API and custom Autocorrelation algorithm
- **Concurrency & Architecture**: Kotlin Coroutines, StateFlow, and ViewModel

## Implementation Steps
**Total Duration:** 3m 36s

### Task_110_ImplementRealTimeMicrophonePitchDetectionForTuner: Implement Real-Time Microphone Pitch Detection for Tuner: 1) Add Manifest.permission.RECORD_AUDIO in AndroidManifest.xml and request runtime permission in TunerScreen.kt. 2) Implement MicrophonePitchDetector.kt using Android AudioRecord API (44100 Hz PCM 16-bit) and Autocorrelation pitch detection algorithm. 3) Connect detected pitch frequency (Hz) to TunerEngine to animate pitch gauge dial needle live in response to instrument sound. 4) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_110_ImplementRealTimeMicrophonePitchDetectionForTuner completed successfully. Implemented Real-Time Microphone Pitch Detection for Tuner: 1) Declared RECORD_AUDIO permission in AndroidManifest.xml. 2) Created MicrophonePitchDetector.kt in com.example.songchords.tuner using AudioRecord API (44.1 kHz, PCM 16-bit) and Autocorrelation pitch detection algorithm. 3) Built TunerViewModel and updated TunerScreen.kt with runtime RECORD_AUDIO permission launcher, microphone listening state, live pitch detection, and animated pitch dial gauge needle responding live to instrument sound input. 4) Added MicrophonePitchDetectorTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 132 passing unit tests.
- **Acceptance Criteria:**
  - RECORD_AUDIO permission requested and declared in AndroidManifest.xml
  - MicrophonePitchDetector captures audio and calculates fundamental frequency in Hz
  - Live pitch needle animates in response to microphone sound input
  - build pass

### Task_111_RunAndVerifyMicrophoneTuner: Verify real-time microphone pitch detection and unit tests on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_111_RunAndVerifyMicrophoneTuner completed successfully. Verified on emulator with 0 crashes. Confirmed real-time microphone pitch detection tuner: 1) RECORD_AUDIO permission requested with '🎙️ Permitir Acceso al Micrófono para Afinar' card and system permission dialog. 2) Live listening state '🎙️ Escuchando la frecuencia del instrumento en vivo…' captures microphone audio via AudioRecord (44.1 kHz, PCM 16-bit) and calculates fundamental frequency in Hz using Autocorrelation algorithm. 3) Animated pitch dial needle responds live to detected instrument sound. 4) Tested Guitar (6-string), Bass (4-string), Bass (5-string) string selection and pure reference tone playback. All 132 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Microphone pitch detection tuner verified functional on emulator
- **Duration:** 3m 36s

