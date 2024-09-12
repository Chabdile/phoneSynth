//package com.example.phonesynth
//
//import android.content.Context
//import android.media.AudioManager
//import android.os.Build
//
//object AudioEngine {
//    // Load native library
//    init {
//        System.loadLibrary("native-lib")
//    }
//
//    fun start() {
//        nativeStart()
//    }
//
//    // Native methods
//    private external fun startStream()
//    private external fun stopStream()
//    private external fun setFrequency()
//    private external fun setAmplitude()
//}
