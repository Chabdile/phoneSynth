package com.example.phonesynth.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.phonesynth.component.*

class InstBellViewModel : ViewModel() {
    private val _state = MutableStateFlow("Hello from InstViewModel")
    val state: StateFlow<String> = _state

    private val audioEngine = AudioEngine()

    fun startStream() {
        audioEngine.startStream()
    }
    fun stopStream() {
        audioEngine.stopStream()
    }
    fun setFrequency(frequency: Float) {
        audioEngine.setFrequency(frequency)
    }
    fun setAmplitude(amplitude: Float) {
        audioEngine.setAmplitude(amplitude)
    }
    fun setWaveform(shape: Int) {
        audioEngine.setWaveform(shape)
    }
    fun setSampleRate(sampleRate: Float) {
        audioEngine.setSampleRate(sampleRate)
    }
    fun setPulseWidth(pulseWidth: Float) {
        audioEngine.setPulseWidth(pulseWidth)
    }

    override fun onCleared() {
        super.onCleared()
        stopStream() // ViewModelが破棄される際にストリームを停止
    }
}