package com.example.phonesynth.connection

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.phonesynth.connection.dto.AppMessage

class ConnectionController(context: Context) {

    // --- Connection State ---
    var isHost = mutableStateOf(true)
    var isRunning = mutableStateOf(false)
    var selectedRoomId = mutableStateOf<String?>(null)
    var myroomName = mutableStateOf("MyRoom")
    var roomList = mutableStateOf(listOf<RoomInfo>())
    var connectedMembers = mutableStateOf(setOf<String>())
    var connectionStatus = mutableStateOf<ConnectionStatus>(ConnectionStatus.Idle)

    private lateinit var connectionManager: ConnectionManager

    // Callbacks to be set by the ViewModel
    var onMessageReceive: ((AppMessage) -> Unit)? = null
    var onRoomListUpdate: ((List<RoomInfo>) -> Unit)? = null
    var onMembersUpdate: ((Set<String>) -> Unit)? = null
    var onStatusUpdate: ((ConnectionStatus) -> Unit)? = null

    init {
        connectionManager = ConnectionManager(
            context = context,
            isHost = isHost.value,
            onMessageReceive = { appMessage ->
                onMessageReceive?.invoke(appMessage)
            },
            onRoomListUpdate = { roomList.value = it; onRoomListUpdate?.invoke(it) },
            onMembersUpdate = { connectedMembers.value = it; onMembersUpdate?.invoke(it) },
            onStatusUpdate = { connectionStatus.value = it; onStatusUpdate?.invoke(it) }
        )
    }

    fun initValues() {
        // --- Connection State ---
        isHost.value = true
        isRunning.value = false
        selectedRoomId.value = null
        myroomName.value = "MyRoom"
    }

    fun start() {
        connectionManager.start(myroomName.value, isHost.value)
        if (isHost.value) {
            isRunning.value = true
        }
    }

    fun stop() {
        connectionManager.stop()
        isRunning.value = false
        connectedMembers.value = emptySet()
        initValues()
    }

    fun connectToRoom(endpointId: String) {
        selectedRoomId.value = endpointId
        isRunning.value = true
        connectionManager.connectToRoom(endpointId)
    }

    fun switchMode() {
        isHost.value = !isHost.value
        if (!isHost.value) start()
        if (isRunning.value) stop()
    }

    fun sendAppMessage(message: AppMessage) {
        connectionManager.sendAppMessage(message)
    }
}