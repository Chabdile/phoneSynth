package com.example.phonesynth.connection

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

data class RoomInfo(val endpointId: String, val info: DiscoveredEndpointInfo)
