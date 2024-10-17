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
//import kotlin.math.sqrt
//import kotlin.math.sin
//import kotlin.math.cos
//import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
//import com.example.phonesynth.ui.ekf
//import com.example.phonesynth.ui.rotateMat


class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    open class Vec(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f
    )

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
//    var rad = Val()
    var angle = Val()

    private var initPressure: Float? = null
    private val A4_FREQUENCY = 440.0f
    private val C4_FREQUENCY = A4_FREQUENCY * 2.0.pow(-9.0 / 12.0).toFloat()
    var transpose = 0
    var pitchShift = 0
    var isSnap = true
    var isClick = false

    val volume = mutableStateOf(0f)
    val tone = mutableStateOf(0f)
    val articulation = mutableStateOf(0f)
    val pressure = mutableStateOf(0f)


    //平滑化パラメータ
    private var previousTone = C4_FREQUENCY
    private var previousVolume = 0.8f
    private var previousArticulation = 0.8f
    private var previousPressure = 0.0f
    private val alpha = 0.5f // スムージング係数

    //timestamps
    private var previousTimestamp: Long = 0L
    private var currentTimestamp: Long = 0L
    private var dt: Double = 0.0

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        resetValues()
    }

    override fun onSensorChanged(event: SensorEvent) {
//        currentTimestamp = event.timestamp
//        dt = (currentTimestamp - previousTimestamp) / 1_000_000_000.0 // ナノ秒を秒に変換

        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyro.x.value = event.values[0]
                gyro.y.value = event.values[1]
                gyro.z.value = event.values[2]
//                gyro.x.value -= highPassFilter(gyro.prevX.value, event.values[0])
//                gyro.y.value -= highPassFilter(gyro.prevY.value, event.values[1])
//                gyro.z.value -= highPassFilter(gyro.prevZ.value, event.values[2])

                // 新しい回転行列からオイラー角を取得
//                rad = getEulerAngles(rad)
//                angle.x.value = normalizeAngle(rad.x)
//                angle.y.value = normalizeAngle(rad.y)
//                angle.z.value = normalizeAngle(rad.z)

//                rad.x.value += integrate(gyro.prevX.value, gyro.x.value)
//                rad.y.value += integrate(gyro.prevY.value, gyro.y.value)
//                rad.z.value += integrate(gyro.prevZ.value, gyro.z.value)

                angle.x.value += normalizeAngle(df(integrate(gyro.prevX.value, gyro.x.value) * dt.toFloat()))
                angle.y.value += normalizeAngle(integrate(gyro.prevY.value, gyro.y.value))
                angle.z.value += normalizeAngle(integrate(gyro.prevZ.value, gyro.z.value))
//                angle.x.value -= highPassFilter(angle.prevX.value, angle.x.value)
//                angle.y.value -= highPassFilter(angle.prevY.value, angle.y.value)
//                angle.z.value -= highPassFilter(angle.prevZ.value, angle.z.value)
//                angle.x.value = lowPassFilter(angle.prevX.value, angle.x.value)
//                angle.y.value = lowPassFilter(angle.prevY.value, angle.y.value)
//                angle.z.value = lowPassFilter(angle.prevZ.value, angle.z.value)

                //--------------------
//                Log.d("angle-xyz: ", "${angle.x.value} \t ${angle.y.value} \t ${angle.z.value}")
                //set tone
                calcTone()
                //--------------------


                gyro.prevX.value = gyro.x.value
                gyro.prevY.value = gyro.y.value
                gyro.prevZ.value = gyro.z.value
//                rad.prevX.value = rad.x.value
//                rad.prevY.value = rad.y.value
//                rad.prevZ.value = rad.z.value
                angle.prevX.value = angle.x.value
                angle.prevY.value = angle.y.value
                angle.prevZ.value = angle.z.value
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accel.x.value = lowPassFilter(accel.x.value, event.values[0])
                accel.y.value = lowPassFilter(accel.y.value, event.values[1])
                accel.z.value = lowPassFilter(accel.z.value, event.values[2])

                //角度alt. 移動するとその際の加速度で音程が変化する
//                angle.x.value = atan2(accel.x.value, sqrt(accel.y.value * accel.y.value + accel.z.value * accel.z.value)) * (180 / Math.PI).toFloat()
//                angle.y.value = atan2(accel.y.value, sqrt(accel.x.value * accel.x.value + accel.z.value * accel.z.value)) * (180 / Math.PI).toFloat()
//                angle.z.value = atan2(accel.z.value, sqrt(accel.x.value * accel.x.value + accel.y.value * accel.y.value)) * (180 / Math.PI).toFloat()


                //--------------------
                //--------------------


//                calcVolume()
//                volume.value = 0.8f
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                //delay normalじゃないとdtが短すぎる
//                linearAccel.x.value = highPassFilter(linearAccel.x.value, event.values[0])
//                linearAccel.y.value = highPassFilter(linearAccel.y.value, event.values[1])
//                linearAccel.z.value = highPassFilter(linearAccel.z.value, event.values[2])
                linearAccel.x.value = lowPassFilter(linearAccel.x.value, event.values[0], 0.8f)
                linearAccel.y.value = lowPassFilter(linearAccel.y.value, event.values[1], 0.8f)
                linearAccel.z.value = lowPassFilter(linearAccel.z.value, event.values[2], 0.8f)

                //
//                velocity.x.value = df(integrate(linearAccel.prevX.value, linearAccel.x.value) * 100)
//                velocity.x.value = highPassFilter(velocity.prevX.value, velocity.x.value)

                velocity.z.value = df(integrate(linearAccel.prevZ.value, linearAccel.z.value) * 100)     //m/sec to cm/sec
//                velocity.z.value = KalmanFilter(velocity.prevZ.value, velocity.z.value)


//                // ハイパスフィルタの適用
                position.z.value += df((velocity.z.value * dt).toFloat())
//                position.z.value = lowPassFilter(position.prevZ.value, position.z.value)
                position.z.value = highPassFilter(position.prevZ.value, position.z.value, 0.2f)
                if (abs(position.z.value) > 200f) position.z.value = 0f
//                position.z.value = KalmanFilter(position.prevZ.value, position.z.value)
//                Pair pred = ekf.ekf(x, u, z, P)

                //--------------------
//                Log.d("l-Accel, velo: ", "${linearAccel.z.value},       ${velocity.z.value},        ${position.z.value}")
                //pitch shift
                if (abs(velocity.prevX.value) <= 16.5 && abs(velocity.x.value) > 16.5) {
                    if (velocity.x.value.sign == 1.0f) {
                        pitchShift += 7
                    } else {
                        pitchShift -= 7
                    }
                }
//                if (angle.prevY.value <= 25 && angle.y.value > 25) pitchShift += 7
//                else if (angle.prevY.value >= -25 && angle.y.value < -25) pitchShift -= 7
                //--------------------



                linearAccel.prevX.value = linearAccel.x.value
                linearAccel.prevY.value = linearAccel.y.value
                linearAccel.prevZ.value = linearAccel.z.value
                velocity.prevX.value = velocity.x.value
                velocity.prevY.value = velocity.y.value
                velocity.prevZ.value = velocity.z.value
                position.prevX.value = position.x.value
                position.prevY.value = position.y.value
                position.prevZ.value = position.z.value
            }
            Sensor.TYPE_PRESSURE -> {
                currentTimestamp = event.timestamp
                dt = (currentTimestamp - previousTimestamp) / 1_000_000_000.0 // ナノ秒を秒に変換

                if (initPressure == null) {
//                    resetValues()
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
        val th = 0.05f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        if (previousTimestamp != 0L) {
            // 台形による積分
//            value = ((curr + prev) / 2 * dt).toFloat()
            value = ((curr + prev) / 2)                                 //時間かけると二重に積分したことになるんじゃ? 20241017:05:21
            value = df(value)
            if (abs(value) < th) value = 0.0f
        }
        return value
    }

    fun differentiate(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.05f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        if (previousTimestamp != 0L) {
            // 擬似微分
            value = ((curr - prev) / dt).toFloat()
            value = df(value)
            if (abs(value) < th) value = 0.0f
        }
        return value
    }

    private fun lowPassFilter(previousValue: Float, currentValue: Float, k: Float = 0.5f): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = (k * curr + (1 - k) * prev)
        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    private fun highPassFilter(previousValue: Float, currentValue: Float, k: Float = 0.25f): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = ((1 - k) * (value + curr - prev)).toFloat()
        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    private fun df(value: Float): Float {
        val df = DecimalFormat("#.####")
        return df.format(value).toFloat()
    }

    // 音teiを更新するメソッド
    fun setScale(semitones: Int): Int {
        //c0を基準としてメジャースケールに調整。c0以下では効果がない
        var degree = semitones + 12*5
        val octave = (degree / 12) - 5
//        if (semitones.sign == -1) degree += 12

        if (isSnap == true) {
            //C major
            if (degree % 12 <= 4) {
                if (degree % 2 == 1) {
                    degree -= 1
                }
            }
            if (degree % 12 > 4) {
                if (degree % 2 == 0) {
                    degree += 1
                }
            }
        }
        Log.d("semitones: ", "${semitones}, \t ${pitchShift}, \t ${octave}, \t ${velocity.x.value}")

        return degree - 12*5
    }

    private fun calcTone() {
        initPressure?.let {
//            val pressureDifference = 0//initPressure!! - pressure.value
            val semitonesChange = (angle.x.value / 15).roundToInt()
            tone.value = 2 * C4_FREQUENCY * 2.0.pow((setScale(semitonesChange + pitchShift) + transpose) / 12.0).toFloat()
        } ?: {
            tone.value = previousTone
        }
        tone.value = lowPassFilter(previousTone, tone.value)
//        tone.value *= sign(angle.z.value)
        previousTone = tone.value
    }

    private fun normalizeAngle(rad: Float): Float {
//        var deg = 0.0
        var normalizedRad = (rad % (2 * Math.PI) / 10) // Normalize to 0 - 2π radians
        if (abs(normalizedRad) >= (2 * Math.PI)) {
            normalizedRad = normalizedRad.sign * (2 * Math.PI) // Ensure positive value
        }
        return Math.toDegrees(normalizedRad).toFloat() // Convert to degrees and normalize to 0 - 360 degrees
//        return normalizedRad
    }

//    // 音量を更新するメソッド
//    private fun calcVolume() {
//        var prev = previousVolume
//        var curr = angle.y.value
//        val th = 10f
//        if (-th < angle.y.value && angle.y.value < th) curr = 1.0f
//        if (angle.y.value in 0.0..90.0) {
//            volume.value = df(1 -  lowPassFilter(prev, curr) / 45)
////            volume.value -= highPassFilter(prev, curr)
//        } else if (angle.y.value < 90.0) {
//            volume.value = 1.0f
//        }
//        previousVolume = volume.value
//    }

    fun isButtonPress(bool: Boolean = false) {
        if (bool == true) {
            volume.value = 0.0f
            if (abs(linearAccel.z.value) < 0.2f && abs(accel.z.value - 9.9f) < 0.03) {
                angle = Val()
            }
        }
        else volume.value = 0.8f
    }

    fun resetValues() {
        accel = Val()
        linearAccel = Val()
        velocity = Val()
        position = Val()
        gyro = Val()
        angle = Val()

//        volume.value = 0f
        tone.value = 2 * C4_FREQUENCY
        pitchShift = 0
//        articulation.value = 0f
        pressure.value = 1000f
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
