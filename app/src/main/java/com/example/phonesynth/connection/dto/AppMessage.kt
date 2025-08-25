package com.example.phonesynth.connection.dto

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
sealed class AppMessage

@Serializable
@SerialName("PARAM_UPDATE")
data class ParamUpdate(
    val paramName: String,
    val value: Float
) : AppMessage()

//@Serializable
//@SerialName("FX_PARAM_UPDATE")
//data class FxParamUpdate(
//    val fxName: String,
//    val paramName: String,
//    val value: Float
//) : AppMessage()
//
//@Serializable
//@SerialName("NOTE_ON")
//data class NoteOn(
//    val note: Int,
//    val velocity: Int = 127
//) : AppMessage()
//
//@Serializable
//@SerialName("NOTE_OFF")
//data class NoteOff(
//    val note: Int
//) : AppMessage()

//@Serializable
//@SerialName("SENSOR_DATA_UPDATE")
//data class SensorDataUpdate(
//    val tone: Float,
//    val volume: Float,
//    val articulation: Float
//) : AppMessage()

//@Serializable
//@SerialName("LOAD_DISTRIBUTION_REQUEST")
//data class LoadDistributionRequest(
//    val calculationId: String,
//    val taskName: String,
//    val params: Map<String, String>
//) : AppMessage()
//
//@Serializable
//@SerialName("LOAD_DISTRIBUTION_RESPONSE")
//data class LoadDistributionResponse(
//    val calculationId: String,
//    val result: List<Float>? = null,
//    val status: String,
//    val errorMessage: String? = null
//) : AppMessage()
