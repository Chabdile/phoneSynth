package com.example.phonesynth.connection.dto

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val appSerializersModule = SerializersModule {
    polymorphic(AppMessage::class) {
        subclass(ParamUpdate::class)
//        subclass(FxParamUpdate::class)
//        subclass(SensorDataUpdate::class)
//        subclass(NoteOn::class)
//        subclass(NoteOff::class)
//        subclass(LoadDistributionRequest::class)
//        subclass(LoadDistributionResponse::class)
    }
}

val appJson = Json {
    ignoreUnknownKeys = true
    serializersModule = appSerializersModule
}
