package com.example.phonesynth.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.phonesynth.component.Sensors.Companion.angle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.phonesynth.component.*

class InstViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state

    val audioController = AudioController()
    val sensors: Sensors = Sensors(application)
    val tone = sensors.repo.tone
    val volume = sensors.repo.volume
    val articulation = sensors.repo.articulation
    private val sensorController = SensorController(sensors)

    fun startStream() { audioController.startStream() }
    fun stopStream() { audioController.stopStream() }
    fun setFrequency(frequency: Float) { audioController.setFrequency(frequency) }
    fun setAmplitude(amplitude: Float) { audioController.setAmplitude(amplitude) }
//    fun setMorphMix(mix: Float) { audioController.setMorphMix(mix) }
    fun setWaveform(shape: Int) { audioController.setWaveform(shape) }
    fun setSampleRate(sampleRate: Float) { audioController.setSampleRate(sampleRate) }
//    fun setPulseWidth(pulseWidth: Float) { audioController.setPulseWidth(pulseWidth) }

    fun updateAudio() {
        sensors.repo.calcTone()
        sensors.repo.calcVolumeY()
    }

    fun mapping() {
        setFrequency(tone.floatValue)
        setAmplitude(volume.floatValue)
//        setMorphMix(articulation.floatValue)
    }

    override fun onCleared() {
        super.onCleared()
        stopStream() // ViewModelが破棄される際にストリームを停止
        sensors.unregisterSensors()
    }
}