package com.example.phonesynth.component

var currentWaveform: Int = 0

class Oboe {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun startStream()
    external fun stopStream()
    external fun setFrequency(frequency: Float)
    external fun setAmplitude(amplitude: Float)
    external fun setWaveform(shape: Int)
    external fun setSampleRate(sampleRate: Float)
//    external fun setPulseWidth(pulseWidth: Float)

    // LFO and Morph controls
    external fun setLfoRate(rate: Float)
    external fun setMorphMix(mix: Float)
    external fun setWaveformA(type: Int)
    external fun setWaveformB(type: Int)

    // Effects Controls
    // Distortion
    external fun enableDistortion(enable: Boolean)
    external fun setDistortionType(type: Int)
    external fun setDistortionDrive(drive: Float)

    // Filter
    external fun enableFilter(enable: Boolean)
    external fun setFilterType(type: Int)
    external fun setFilterCutoff(cutoff: Float)
    external fun setFilterResonance(resonance: Float)

    // Chorus
    external fun enableChorus(enable: Boolean)
    external fun setChorusRate(rate: Float)
    external fun setChorusDepth(depth: Float)
    external fun setChorusMix(mix: Float)

    // Reverb
    external fun enableReverb(enable: Boolean)
    external fun setReverbRoomSize(roomSize: Float)
    external fun setReverbDamping(damping: Float)
    external fun setReverbMix(mix: Float)

    // Compressor
    external fun enableCompressor(enable: Boolean)
    external fun setCompressorThreshold(threshold: Float)
    external fun setCompressorRatio(ratio: Float)
    external fun setCompressorAttack(attack: Float)
    external fun setCompressorRelease(release: Float)
    external fun setCompressorMakeupGain(gain: Float)
}



//effects

//    // distortion
//    external fun setDistortion(type: String?, drive: Float)
//
//    // 例: ソフトクリッピングディストーションを設定
//    fun applySoftClipping(view: View?) {
//        setDistortion("soft", 2.0f) // ドライブ量を2.0に設定
//    }
//
//    // 例: ハードクリッピングディストーションを設定
//    fun applyHardClipping(view: View?) {
//        setDistortion("hard", 3.0f) // ドライブ量を3.0に設定
//    }

//  //comp
//    external fun setCompressorSettings(threshold: Float, ratio: Float, attack: Float, release: Float, gain: Float)
//
//// 例: コンプレッサの設定
//    fun configureCompressor(view: View) {
//        setCompressorSettings(-20.0f, 4.0f, 0.01f, 0.1f, 1.5f)
