package com.example.phonesynth.component

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf

enum class AudioParam(val paramName: String) {
    NONE("none"),
    TONE("tone"),
    VOLUME("volume"),
    ARTICULATION("articulation");

    companion object {
        fun fromString(name: String): AudioParam? {
            return entries.find { it.paramName == name }
        }
    }
}

//val paramKeys = AudioParam.entries.map { it.paramName }

fun paramMapping(sensors: Sensors): Map<AudioParam, MutableState<Float>> {
    val paramList = listOf(
        mutableFloatStateOf(0f),
        sensors.repo.tone,
        sensors.repo.volume,
        sensors.repo.articulation,
    )

    return AudioParam.entries.toTypedArray().zip(paramList).toMap()
}