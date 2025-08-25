package com.example.phonesynth

import com.example.phonesynth.component.Sensors

class MIDI(private val sensors: Sensors) {

    /**
     * 指定されたMIDIノート番号に対応する音を再生します。
     * velocityは音量に影響します（現在は未実装）。
     */
    fun noteOn(note: Int, velocity: Int) {
        // MIDIノート番号を周波数に変換
        val frequency = 440.0 * Math.pow(2.0, (note - 69.0) / 12.0)
        sensors.repo.tone.value = frequency.toFloat()
        sensors.repo.isMuted = true // play sound
    }

    /**
     * 指定されたMIDIノート番号の音を停止します。
     */
    fun noteOff(note: Int) {
        sensors.repo.isMuted = false // stop sound
    }

    /**
     * 音量を設定します。
     */
    fun setVolume(volume: Float) {
        sensors.repo.volume.value = volume
    }
}
