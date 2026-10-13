package com.example.songchords.ui.tuner

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.songchords.tuner.MicrophonePitchDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TunerViewModel(
    private val pitchDetector: MicrophonePitchDetector = MicrophonePitchDetector(),
) : ViewModel() {

    private val _selectedInstrument = MutableStateFlow(InstrumentType.GUITAR_6)
    val selectedInstrument: StateFlow<InstrumentType> = _selectedInstrument.asStateFlow()

    private val _selectedStringIndex = MutableStateFlow(0)
    val selectedStringIndex: StateFlow<Int> = _selectedStringIndex.asStateFlow()

    private val _hasMicPermission = MutableStateFlow(false)
    val hasMicPermission: StateFlow<Boolean> = _hasMicPermission.asStateFlow()

    private val _isListening = MutableStateFlow(false)

    private val _detectedFrequency = MutableStateFlow(0f)
    val detectedFrequency: StateFlow<Float> = _detectedFrequency.asStateFlow()

    private val _simulatedCentsOffset = MutableStateFlow(0.0)
    val simulatedCentsOffset: StateFlow<Double> = _simulatedCentsOffset.asStateFlow()

    private val _isLivePitchActive = MutableStateFlow(false)
    val isLivePitchActive: StateFlow<Boolean> = _isLivePitchActive.asStateFlow()

    fun checkMicPermission(context: Context) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        _hasMicPermission.value = granted
        if (granted && !_isListening.value) {
            startListening()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _hasMicPermission.value = granted
        if (granted) {
            startListening()
        } else {
            stopListening()
        }
    }

    fun selectInstrument(instrument: InstrumentType) {
        _selectedInstrument.value = instrument
        _selectedStringIndex.value = 0
        _simulatedCentsOffset.value = 0.0
        _detectedFrequency.value = 0f
        _isLivePitchActive.value = false
    }

    fun selectStringIndex(index: Int) {
        _selectedStringIndex.value = index
        _simulatedCentsOffset.value = 0.0
        _detectedFrequency.value = 0f
        _isLivePitchActive.value = false
    }

    fun updateSimulatedCents(cents: Double) {
        _isLivePitchActive.value = false
        _simulatedCentsOffset.value = cents
    }

    fun startListening() {
        if (_isListening.value) return
        _isListening.value = true
        pitchDetector.startListening { pitch ->
            _detectedFrequency.value = pitch
            _isLivePitchActive.value = true
            val strings = TunerEngine.getStringsForInstrument(_selectedInstrument.value)
            val targetString = strings.getOrElse(_selectedStringIndex.value) { strings.first() }
            val cents = TunerEngine.calculateCentsOffset(pitch.toDouble(), targetString.frequencyHz)
            _simulatedCentsOffset.value = cents
        }
    }

    fun stopListening() {
        _isListening.value = false
        _isLivePitchActive.value = false
        pitchDetector.stopListening()
    }

    override fun onCleared() {
        stopListening()
    }
}
