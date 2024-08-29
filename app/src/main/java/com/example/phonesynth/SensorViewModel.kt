package com.example.phonesynth

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.annotation.ReturnThis
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.AndroidViewModel
import kotlin.contracts.Returns
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
//    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    val x = mutableStateOf(0f)
    val y = mutableStateOf(0f)
    val z = mutableStateOf(0f)

    val volume = mutableStateOf(0f)
    val tone = mutableStateOf(0f)
    val articulation = mutableStateOf(0f)

    val pressure = mutableStateOf(0f)

    private var initPressure: Float? = null
    private val A4_FREQUENCY = 440.0f
    private val C4_FREQUENCY = A4_FREQUENCY * 2.0.pow(-9.0 / 12.0).toFloat()

    //平滑化パラメータ
    private var previousTone = A4_FREQUENCY
    private var previousVolume = 0.8f

    private val alpha = 0.5 // スムージング係数

    //
    private var previousTimestamp: Long = 0L

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val gX = event.values[0]
                val gY = event.values[1]
                val gZ = event.values[2]

//                Integrate

                x.value = atan2(gX, sqrt(gY * gY + gZ * gZ)) * (180 / Math.PI).toFloat()
                y.value = atan2(gY, sqrt(gX * gX + gZ * gZ)) * (180 / Math.PI).toFloat()
                z.value = atan2(gZ, sqrt(gX * gX + gY * gY)) * (180 / Math.PI).toFloat()

                // 0 to 180 mapped to 0 to 1
//                volume.value = when((roll.value + 90) / 90f) {
//                    in 0..2 -> (roll.value + 90) / 90f)
//                    else -> 0
//                }
                updateVolume(0.8f)
                updateTone(calcToneFromPressure() * 2)
                articulation.value = 0.8f
            }
            Sensor.TYPE_PRESSURE -> {
                if (initPressure == null) {
                    initPressure = event.values[0]
                }
//                val tmp = (initPressure!!.toDouble() - initPressure!!.roundToInt()) * 10
//                tone.value = (Math.pow(tmp, 2.0)).toFloat()
                pressure.value = event.values[0]
            }
        }
        previousTimestamp = event.timestamp
    }

    // 音程を更新するメソッド
    fun updateTone(currentTone: Float) {
        // 指数移動平均で平滑化
        val smoothedTone = alpha * currentTone + (1 - alpha) * previousTone
        tone.value = smoothedTone.toFloat()

        // 変化率の制限
        val maxChange = 100.0 // 設定した最大変化率
        if (abs(smoothedTone - previousTone) > maxChange) {
            tone.value = (previousTone + sign(smoothedTone - previousTone) * maxChange).toFloat()
        }

        previousTone = tone.value
    }

    // 音量を更新するメソッド
    fun updateVolume(currentVolume: Float) {
        // 指数移動平均で平滑化
        val smoothedVolume = alpha * currentVolume + (1 - alpha) * previousVolume
        volume.value = smoothedVolume.toFloat()

        // 変化率の制限
        val maxChange = 0.05f // 設定した最大変化率
        if (abs(smoothedVolume - previousVolume) > maxChange) {
            volume.value = (previousVolume + sign(smoothedVolume - previousVolume) * maxChange).toFloat()
        }
        previousVolume = volume.value
    }

    fun integrate(previousValue: Float, currentValue: Float, currentTimestamp: Long): Double {
        var velocity = 0.0
        if (previousTimestamp != 0L) {
            val dt = (currentTimestamp - previousTimestamp) / 1_000_000_000.0 // ナノ秒を秒に変換
            // 台形による積分: (前回の加速度 + 今回の加速度) / 2 * dt
            val ds = ((previousValue + currentValue) / 2) * dt
            velocity += ds
        }

        return velocity
    }


    fun resetInitPressure() {
        initPressure = null
    }

    private fun calcToneFromPressure() : Float {
        initPressure?.let {
            val pressureDifference = 0//initPressure!! - pressure.value
            val semitonesChange = (y.value / 15).roundToInt()
            return C4_FREQUENCY * 2.0.pow(semitonesChange / 12.0).toFloat() + pressureDifference
        }?: return C4_FREQUENCY
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
