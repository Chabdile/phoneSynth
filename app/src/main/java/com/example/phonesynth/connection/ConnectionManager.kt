package com.example.phonesynth.connection

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.example.phonesynth.connection.dto.AppMessage
import com.example.phonesynth.connection.dto.appJson
import kotlinx.serialization.encodeToString

class ConnectionManager(
    context: Context,
    private var isHost: Boolean,
    private val onMessageReceive: (AppMessage) -> Unit,
    private val onRoomListUpdate: (List<RoomInfo>) -> Unit,
    private val onMembersUpdate: (Set<String>) -> Unit,
    private val onStatusUpdate: (ConnectionStatus) -> Unit,
) {
    private val client = Nearby.getConnectionsClient(context)
    private lateinit var roomName: String
    private val serviceId = "com.example.phonesynth"
    private val json = appJson
    private val connectedMembers = mutableSetOf<String>()
    private val discoveredRooms = mutableMapOf<String, DiscoveredEndpointInfo>()
    private var masterEndpointId: String? = null

    fun start(roomname: String, host: Boolean) {
        val strategy = Strategy.P2P_STAR
        isHost = host
        roomName = roomname

        if (isHost) {
            client.startAdvertising(
                roomName,
                serviceId,
                lifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(strategy).build()
            )
            .addOnSuccessListener {
                Log.d("Nearby", "Advertising started")
                onStatusUpdate(ConnectionStatus.Advertising)
            }
            .addOnFailureListener { e ->
                Log.e("Nearby", "Advertising failed", e)
                onStatusUpdate(ConnectionStatus.Error("Advertising failed"))
            }
        } else {
            client.startDiscovery(
                serviceId,
                discoveryCallback,
                DiscoveryOptions.Builder().setStrategy(strategy).build()
            )
            .addOnSuccessListener {
                Log.d("Nearby", "Discovery started")
                onStatusUpdate(ConnectionStatus.Discovering)
            }
            .addOnFailureListener { e ->
                Log.e("Nearby", "Discovery failed", e)
                onStatusUpdate(ConnectionStatus.Error("Discovery failed"))
            }
        }
    }

    fun stop() {
        client.stopAllEndpoints()
        synchronized(this) {
            connectedMembers.clear()
            discoveredRooms.clear()
        }
        onRoomListUpdate(emptyList())
        onMembersUpdate(emptySet())
        onStatusUpdate(ConnectionStatus.Idle)

        Log.d("Nearby", "stopped")
    }

    fun connectToRoom(endpointId: String) {
        Log.d("Nearby", "(connectToRoom)endpointId: ${endpointId}, discoveredRooms.keys: ${discoveredRooms.keys}")

        client.requestConnection("Slave", endpointId, lifecycleCallback)
        client.stopDiscovery()
    }

    private val discoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            synchronized(this) {
                discoveredRooms[endpointId] = info
                onRoomListUpdate(discoveredRooms.map { RoomInfo(it.key, it.value) })
            }

            Log.d("Nearby", "connectedMembers: ${connectedMembers}}")
        }

        override fun onEndpointLost(endpointId: String) {
            synchronized(this) {
                discoveredRooms.remove(endpointId)
                onRoomListUpdate(discoveredRooms.map { RoomInfo(it.key, it.value) })
            }
        }
    }

    private val lifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            client.acceptConnection(endpointId, payloadCallback)
            Log.d("Nearby", "endpointId: ${endpointId}, discoveredRooms[endpointId]: ${discoveredRooms[endpointId]}")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                onStatusUpdate(ConnectionStatus.Connected(endpointId))

                if (isHost) {
                    synchronized(this) {
                        connectedMembers.add(endpointId)
                    }
                } else {
                    masterEndpointId = endpointId
                }
                onMembersUpdate(connectedMembers)
            } else {
                onStatusUpdate(ConnectionStatus.Error("Connection failed"))
            }
        }

        override fun onDisconnected(endpointId: String) {
            onStatusUpdate(ConnectionStatus.Disconnected)

            synchronized(this) {
                connectedMembers.remove(endpointId)
            }
            if (!isHost && endpointId == masterEndpointId) {
                masterEndpointId = null
            }
            onMembersUpdate(connectedMembers)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val msg = payload.asBytes()?.toString(Charsets.UTF_8)
            if (msg != null) {
                try {
                    val appMessage = appJson.decodeFromString<AppMessage>(msg)
                    onMessageReceive(appMessage)
                } catch (e: Exception) {
                    Log.e("Nearby", "Error decoding message: ${e.message}")
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            //
        }
    }

    fun sendAppMessage(appMessage: AppMessage) {
        val text = json.encodeToString(appMessage)
        val payload = Payload.fromBytes(text.toByteArray())
        if (isHost) {
            for (id in connectedMembers) {
                client.sendPayload(id, payload)
            }
        } else {
            masterEndpointId?.let {
                client.sendPayload(it, payload)
            }
        }
    }
}
