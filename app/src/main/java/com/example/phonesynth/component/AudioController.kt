package com.example.phonesynth.component

class AudioController {
    private val audioEngine = Oboe()

    fun startStream() { audioEngine.startStream() }
    fun stopStream() { audioEngine.stopStream() }
    fun setFrequency(frequency: Float) { audioEngine.setFrequency(frequency) }
    fun setAmplitude(amplitude: Float) { audioEngine.setAmplitude(amplitude) }
    fun setWaveform(shape: Int) { audioEngine.setWaveform(shape) }
    fun setSampleRate(sampleRate: Float) { audioEngine.setSampleRate(sampleRate) }
//    fun setPulseWidth(pulseWidth: Float) { audioEngine.setPulseWidth(pulseWidth) }

    // LFO and Morph controls
    fun setLfoRate(rate: Float) { audioEngine.setLfoRate(rate) }
    fun setMorphMix(mix: Float) { audioEngine.setMorphMix(mix) }
    fun setWaveformA(type: Int) { audioEngine.setWaveformA(type) }
    fun setWaveformB(type: Int) { audioEngine.setWaveformB(type) }

    // Effects Controls
    // Distortion
    fun enableDistortion(enable: Boolean) { audioEngine.enableDistortion(enable) }
    fun setDistortionType(type: Int) { audioEngine.setDistortionType(type) }
    fun setDistortionDrive(drive: Float) { audioEngine.setDistortionDrive(drive) }

    // Filter
    fun enableFilter(enable: Boolean) { audioEngine.enableFilter(enable) }
    fun setFilterType(type: Int) { audioEngine.setFilterType(type) }
    fun setFilterCutoff(cutoff: Float) { audioEngine.setFilterCutoff(cutoff) }
    fun setFilterResonance(resonance: Float) { audioEngine.setFilterResonance(resonance) }

    // Chorus
    fun enableChorus(enable: Boolean) { audioEngine.enableChorus(enable) }
    fun setChorusRate(rate: Float) { audioEngine.setChorusRate(rate) }
    fun setChorusDepth(depth: Float) { audioEngine.setChorusDepth(depth) }
    fun setChorusMix(mix: Float) { audioEngine.setChorusMix(mix) }

    // Reverb
    fun enableReverb(enable: Boolean) { audioEngine.enableReverb(enable) }
    fun setReverbRoomSize(roomSize: Float) { audioEngine.setReverbRoomSize(roomSize) }
    fun setReverbDamping(damping: Float) { audioEngine.setReverbDamping(damping) }
    fun setReverbMix(mix: Float) { audioEngine.setReverbMix(mix) }

    // Compressor
    fun enableCompressor(enable: Boolean) { audioEngine.enableCompressor(enable) }
    fun setCompressorThreshold(threshold: Float) { audioEngine.setCompressorThreshold(threshold) }
    fun setCompressorRatio(ratio: Float) { audioEngine.setCompressorRatio(ratio) }
    fun setCompressorAttack(attack: Float) { audioEngine.setCompressorAttack(attack) }
    fun setCompressorRelease(release: Float) { audioEngine.setCompressorRelease(release) }
    fun setCompressorMakeupGain(gain: Float) { audioEngine.setCompressorMakeupGain(gain) }
}