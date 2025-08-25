package com.example.phonesynth.component

class SensorController(val sensors: Sensors) {
    val tone = sensors.repo.tone
    val volume = sensors.repo.volume
    val articulation = sensors.repo.articulation

    fun unregisterSensors() {
        sensors.unregisterSensors()
    }

    var isMuted: Boolean
        get() = sensors.repo.isMuted
        set(value) { sensors.repo.isMuted = value }

    fun calcTone() {
        sensors.repo.calcTone()
        tone.floatValue = sensors.repo.df2(tone.floatValue)
    }

    fun calcVolume() {
        sensors.repo.calcVolume()
        volume.floatValue = sensors.repo.df2(volume.floatValue)
    }

    fun calcArticulation() {
        sensors.repo.calcArticulation()
        articulation.floatValue = sensors.repo.df2(articulation.floatValue)
    }
}