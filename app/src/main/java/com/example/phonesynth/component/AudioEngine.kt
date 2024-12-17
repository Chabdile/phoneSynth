package com.example.phonesynth.component

//import androidx.compose.runtime.Composable
//import androidx.lifecycle.viewmodel.compose.viewModel

//.cppと共有したいけど決め打ち
//val waveform = arrayOf(
//    "Sine",
//    "Triangle",
//    "Sawtooth",
//    "Square"
//)
//wave shape -> 0~3


var currentWaveform: Int = 0

class AudioEngine {
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
    external fun setPulseWidth(pulseWidth: Float)
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
