package com.example.phonesynth

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    open class Val(
        var x: MutableState<Float> = mutableStateOf(0f),    //sensor raw val
        var y: MutableState<Float> = mutableStateOf(0f),
        var z: MutableState<Float> = mutableStateOf(0f),
        var prevX: MutableState<Float> = mutableStateOf(0f),
        var prevY: MutableState<Float> = mutableStateOf(0f),
        var prevZ: MutableState<Float> = mutableStateOf(0f)
    )
    //センサ
    var accel = Val()
    var linearAccel = Val()
    var velocity = Val()
    var position = Val()
    var gyro = Val()
    var angle = Val()

    val volume = mutableStateOf(0f)
    val tone = mutableStateOf(0f)
    val articulation = mutableStateOf(0f)
    val pressure = mutableStateOf(0f)

    private var initPressure: Float? = null
    private val A4_FREQUENCY = 440.0f
    private val C4_FREQUENCY = A4_FREQUENCY * 2.0.pow(-9.0 / 12.0).toFloat()

    //平滑化パラメータ
    private var previousTone = C4_FREQUENCY
    private var previousVolume = 0.8f
    private var previousArticulation = 0.8f
    private var previousPressure = 0.0f
    private val alpha = 0.5 // スムージング係数

    //kalman
    private var errorCovariance = 1.0  // 誤差共分散

    //timestamps
    private var previousTimestamp: Long = 0L
    private var currentTimestamp: Long = 0L
    private var dt: Double = 0.0

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent) {
//        currentTimestamp = event.timestamp
//        dt = (currentTimestamp - previousTimestamp) / 1_000_000_000.0 // ナノ秒を秒に変換

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accel.x.value = lowPassFilter(accel.x.value, event.values[0])
                accel.y.value = lowPassFilter(accel.y.value, event.values[1])
                accel.z.value = lowPassFilter(accel.z.value, event.values[2])

                //移動するとその際の加速度で音程が変化する
//                angle.x.value = atan2(accel.x.value, sqrt(accel.y.value * accel.y.value + accel.z.value * accel.z.value)) * (180 / Math.PI).toFloat()
//                angle.y.value = atan2(accel.y.value, sqrt(accel.x.value * accel.x.value + accel.z.value * accel.z.value)) * (180 / Math.PI).toFloat()
//                angle.z.value = atan2(accel.z.value, sqrt(accel.x.value * accel.x.value + accel.y.value * accel.y.value)) * (180 / Math.PI).toFloat()

                // 0 to 180 mapped to 0 to 1
//                volume.value = when((roll.value + 90) / 90f) {
//                    in 0..2 -> (roll.value + 90) / 90f)
//                    else -> 0
//                }
                if (abs(accel.z.value + 9.8f) < 0.1) {
                    resetValues()
                }
//                calcVolume()
                volume.value = 0.8f
//                calcTone()
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyro.x.value = lowPassFilter(gyro.x.value, event.values[0])
                gyro.y.value = lowPassFilter(gyro.y.value, event.values[1])
                gyro.z.value = lowPassFilter(gyro.z.value, event.values[2])

                angle.x.value += normalizeAngle(integrate(gyro.prevX.value, gyro.x.value))
                angle.y.value += normalizeAngle(integrate(gyro.prevY.value, gyro.y.value))
                angle.z.value += normalizeAngle(integrate(gyro.prevZ.value, gyro.z.value))

                angle.x.value = lowPassFilter(angle.prevX.value, angle.x.value)
                angle.y.value = lowPassFilter(angle.prevY.value, angle.y.value)
                angle.z.value = lowPassFilter(angle.prevZ.value, angle.z.value)
//                angle.x.value -= highPassFilter(angle.prevX.value, angle.x.value)
//                angle.y.value -= highPassFilter(angle.prevY.value, angle.y.value)
//                angle.z.value -= highPassFilter(angle.prevZ.value, angle.z.value)

                Log.d("angle-xyz: ", "${angle.x.value} \t ${angle.y.value} \t ${angle.z.value}")
                calcTone()

                gyro.prevX.value = gyro.x.value
                gyro.prevY.value = gyro.y.value
                gyro.prevZ.value = gyro.z.value
                angle.prevX.value = angle.x.value
                angle.prevY.value = angle.y.value
                angle.prevZ.value = angle.z.value
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                currentTimestamp = event.timestamp
                dt = (currentTimestamp - previousTimestamp) / 1_000_000_000.0 // ナノ秒をm秒に変換
                //
                linearAccel.x.value = lowPassFilter(linearAccel.x.value, event.values[0])
                linearAccel.y.value = lowPassFilter(linearAccel.y.value, event.values[1])
                linearAccel.z.value = lowPassFilter(linearAccel.z.value, event.values[2])

//                // ハイパスフィルタの適用
//                velocity.z.value = (alpha * (velocity.z.value + linearAccel.z.value - linearAccel.prevZ.value)).toFloat()
                velocity.z.value += integrate(linearAccel.prevZ.value, linearAccel.z.value)
//                velocity.z.value = KalmanFilter(velocity.prevZ.value, velocity.z.value)

                velocity.z.value = df((velocity.z.value * 0.01).toFloat()) * 36   //reduce alpha

//                // ハイパスフィルタの適用
//                position.z.value = (alpha * (position.z.value + velocity.z.value - velocity.prevZ.value)).toFloat()
                position.z.value += df(integrate(velocity.prevZ.value, velocity.z.value))
                position.z.value = KalmanFilter(position.prevZ.value, position.z.value)


//                Log.d("l-Accel, velo: ", "${linearAccel.z.value},       ${velocity.z.value},        ${position.z.value}")

                linearAccel.prevX.value = linearAccel.x.value
                linearAccel.prevY.value = linearAccel.y.value
                linearAccel.prevZ.value = linearAccel.z.value
                velocity.prevX.value = velocity.x.value
                velocity.prevY.value = velocity.y.value
                velocity.prevZ.value = velocity.z.value
            }
            Sensor.TYPE_PRESSURE -> {
                if (initPressure == null) {
                    initPressure = event.values[0]
                }
//                val tmp = (initPressure!!.toDouble() - initPressure!!.roundToInt()) * 10
//                tone.value = (Math.pow(tmp, 2.0)).toFloat()
                // ハイパスフィルタの適用
                pressure.value -= highPassFilter(previousPressure, pressure.value)

                previousPressure = pressure.value
                pressure.value = event.values[0]
            }
        }

        previousTimestamp = currentTimestamp
    }

    private fun integrate(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        if (previousTimestamp != 0L) {
            // 台形による積分
            value = ((curr + prev) / 2 * dt).toFloat()
            value = df(value)
            if (abs(value) < th) value = 0.0f
        }
        return value
    }

    fun differentiate(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        if (previousTimestamp != 0L) {
            // 台形による積分
            value = ((curr - prev) / dt).toFloat()
            value = df(value)
            if (abs(value) < th) value = 0.0f
        }
        return value
    }

    private fun lowPassFilter(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.2f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = (alpha * curr + (1 - alpha) * prev).toFloat()
        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    private fun highPassFilter(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.2f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = (0.2 * (value + curr - prev)).toFloat()
        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    private fun df(value: Float): Float {
        val df = DecimalFormat("#.####")
        return df.format(value).toFloat()
    }

    fun KalmanFilter(estimate:Float, measurement: Float): Float {
        var es = estimate
//        var errorCovariance = 1.0  // 誤差共分散
        val processNoise = 1e-5  // プロセスノイズ
        val measurementNoise = 1e-2  // 測定ノイズ
        var kalmanGain = 0.0  // カルマンゲイン

        // 状態の予測（旧のフィルタ済みセンサ値を使用）
//        es = (es + controlInput * dt).toFloat()

        // 誤差共分散の更新（プロセスノイズを加算）
//        errorCovariance += processNoise

        // カルマンゲインの計算
        kalmanGain = errorCovariance / (errorCovariance + measurementNoise)

        // 状態の更新
        es = (es + kalmanGain * (measurement - es)).toFloat()

        // 誤差共分散の更新
        errorCovariance = (1 - kalmanGain) * errorCovariance + processNoise

        return es
    }

    private fun calcTone() {
        initPressure?.let {
            val pressureDifference = 0//initPressure!! - pressure.value
            val semitonesChange = (angle.x.value / 15).roundToInt()
            tone.value = 2 * C4_FREQUENCY * 2.0.pow(semitonesChange / 12.0).toFloat() + pressureDifference
        }?: {
            tone.value = previousTone
        }
        tone.value = lowPassFilter(previousTone, tone.value)
//        tone.value *= sign(angle.z.value)
        previousTone = tone.value
    }

    // 音量を更新するメソッド
    private fun calcVolume() {
        var prev = previousVolume
        var curr = accel.z.value / 10
        val th = 3f
        if (-90.0 - th < angle.z.value && angle.z.value < -90.0 + th) curr = 1f
        if (-90.0 < angle.z.value && angle.z.value < 0.0) {
            volume.value = lowPassFilter(prev, curr)
            previousVolume = volume.value
        } else {
            volume.value = 0.0f
        }
    }

    // 音量を更新するメソッド
    private fun normalizeAngle(rad: Float): Float {
//        var deg = 0.0
        var normalizedRad = rad % (1 * Math.PI) // Normalize to 0 - 2π radians
        if (abs(normalizedRad) >= (1 * Math.PI)) {
            normalizedRad = sign(normalizedRad) * (1 * Math.PI) // Ensure positive value
        }
        return Math.toDegrees(normalizedRad).toFloat() // Convert to degrees and normalize to 0 - 360 degrees
//        return normalizedRad
    }

    fun resetValues() {
        accel = Val()
        linearAccel = Val()
        velocity = Val()
        position = Val()
        gyro = Val()
        angle = Val()

        volume.value = 0f
        tone.value = 0f
        articulation.value = 0f
        pressure.value = 0f
        initPressure = null
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
