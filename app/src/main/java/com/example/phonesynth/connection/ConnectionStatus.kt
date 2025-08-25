package com.example.phonesynth.connection

sealed class ConnectionStatus {
    data object Idle : ConnectionStatus()
    data object Advertising : ConnectionStatus()
    data object Discovering : ConnectionStatus()
    data class Connected(val endpointId: String) : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
    data object Disconnected : ConnectionStatus()
}
