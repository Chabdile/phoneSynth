package com.example.phonesynth.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.example.phonesynth.component.AudioController
import com.example.phonesynth.component.SensorController
import com.example.phonesynth.component.ParameterProcessor
import com.example.phonesynth.component.Repository
import com.example.phonesynth.component.Sensors
import com.example.phonesynth.connection.ConnectionController
import com.example.phonesynth.connection.dto.ParamUpdate

class InstEnsembleViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state

    // --- Audio Dependencies ---
    val audioController = AudioController()
    val sensors: Sensors = Sensors(application)
    val tone = sensors.repo.tone
    val volume = sensors.repo.volume
    val articulation = sensors.repo.articulation
    private val sensorController = SensorController(sensors)

    // --- Connection Dependencies ---
    private val connectionController = ConnectionController(application.applicationContext)

    // --- Parameter Processing ---
    val parameterProcessor =
        ParameterProcessor(sensorController, audioController, connectionController)

    // Expose relevant states from controllers
    val isHost = connectionController.isHost
    val isRunning = connectionController.isRunning
    val selectedRoomId = connectionController.selectedRoomId
    val myroomName = connectionController.myroomName
    val roomList = connectionController.roomList
    val connectedMembers = connectionController.connectedMembers
    val connectionStatus = connectionController.connectionStatus

    val receivedParam = parameterProcessor.receivedParam
    val receivedValue = parameterProcessor.receivedValue
    val selectedKey = parameterProcessor.selectedKey

    fun startStream() { audioController.startStream() }
    fun stopStream() { audioController.stopStream() }
//    fun setFrequency(frequency: Float) { audioController.setFrequency(frequency) }
//    fun setAmplitude(amplitude: Float) { audioController.setAmplitude(amplitude) }
//    fun setMorphMix(mix: Float) { audioController.setMorphMix(mix) }
//    fun setSampleRate(sampleRate: Float) { audioController.setSampleRate(sampleRate) }

//    fun setPulseWidth(pulseWidth: Float) {
//        audioController.setPulseWidth(pulseWidth)
//    }

    // =============================================================================================

    init {
        // Set up callbacks for ConnectionController
        connectionController.onMessageReceive = { appMessage ->
            if (appMessage is ParamUpdate) {
                parameterProcessor.processReceivedParam(appMessage)
            }
        }
        // Other callbacks can be set here if needed
    }

    fun startConnection() {
        connectionController.start()
    }

    fun stopConnection() {
        connectionController.stop()
        initValues()
    }

    fun connectToRoom(endpointId: String) {
        connectionController.connectToRoom(endpointId)
    }

    fun switchMode() {
        connectionController.switchMode()
    }

    fun updateAudio() {
        parameterProcessor.updateAudio()
    }

    fun mapping() {
        parameterProcessor.mapping()
    }

    fun initValues() {
        // --- Connection State ---
        connectionController.initValues()
//        roomList = mutableStateOf(listOf<RoomInfo>()) // These are now managed by ConnectionController
//        connectedMembers = mutableStateOf(setOf<String>()) // These are now managed by ConnectionController
//        connectionStatus = mutableStateOf<ConnectionStatus>(ConnectionStatus.Idle) // These are now managed by ConnectionController


        // -- Parameter State ---
        parameterProcessor.initValues()
    }


    // =============================================================================================

    // region Lifecycle
    override fun onCleared() {
        super.onCleared()
        connectionController.stop() // Stop connection when ViewModel is destroyed
        audioController.stopStream() // Stop audio stream when ViewModel is destroyed
        sensorController.unregisterSensors()
    }
}