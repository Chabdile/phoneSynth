package com.example.phonesynth.component

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.phonesynth.connection.ConnectionController
import com.example.phonesynth.connection.dto.ParamUpdate

class ParameterProcessor(
    private val sensorController: SensorController,
    private val audioController: AudioController,
    private val connectionController: ConnectionController
) {
    // --- Parameter State ---
    var receivedParam = mutableStateOf("")
    var receivedValue = mutableFloatStateOf(0f)
    val paramRemap: Map<AudioParam, MutableState<Float>> = paramMapping(sensorController.sensors)
    var prevSendParam = mutableFloatStateOf(0.0f)
    var selectedKey = mutableStateOf(AudioParam.NONE)

    fun processReceivedParam(appMessage: ParamUpdate) {
        val audioParam = AudioParam.fromString(appMessage.paramName)
        if (audioParam != null && paramRemap.containsKey(audioParam)) {
            paramRemap[audioParam]!!.value = appMessage.value
        } else {
            Log.d("paramRemap", "unexpected unknown paramName: ${appMessage.paramName}")
        }

        receivedParam.value = appMessage.paramName
        receivedValue.floatValue = appMessage.value


        Log.d("Nearby", "get: ${appMessage.paramName}, ${appMessage.value}")
    }

    fun updateAudio() {
        if (connectionController.connectedMembers.value.isEmpty()) sensorController.isMuted = true
        when (selectedKey.value) {
            AudioParam.NONE -> sensorController.isMuted = false // If no specific param selected, unmute
            AudioParam.TONE -> sensorController.calcTone()
            AudioParam.VOLUME -> sensorController.calcVolume()
            AudioParam.ARTICULATION -> sensorController.calcArticulation()
            else -> { /* Do nothing for other params */ }
        }
    }

    fun mapping() {
        //send and map value
        if (connectionController.isHost.value) {
            // host: get param, mapping
            if (connectionController.connectedMembers.value.isNotEmpty()) {
                audioController.setFrequency(sensorController.tone.floatValue)
                audioController.setAmplitude(sensorController.volume.floatValue)
                audioController.setMorphMix(sensorController.articulation.floatValue)
            } else {
                sensorController.volume.floatValue = 0.0f
            }
        } else {
            // guest: send param
            if (prevSendParam.floatValue != paramRemap[selectedKey.value]!!.value) {
                connectionController.sendAppMessage(ParamUpdate(selectedKey.value.paramName, paramRemap[selectedKey.value]!!.value))
                prevSendParam.floatValue = paramRemap[selectedKey.value]!!.value
            }
        }
    }

    fun initValues() {
        // -- Parameter State ---
        receivedParam.value = ""
        receivedValue.floatValue = 0f

        selectedKey.value = AudioParam.NONE
    }
}