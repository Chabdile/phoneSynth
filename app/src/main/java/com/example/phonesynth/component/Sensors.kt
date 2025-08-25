package com.example.phonesynth.component

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.math.abs
import kotlin.math.sign

class Sensors (application: Application) : SensorEventListener {
    private val sensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
	private val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//    private val gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    val repo = Repository()
    var currentQuat: FloatArray = FloatArray(4)  // [w, x, y, z] ?

    //現在と過去のセンサ値
    open class Val(
        var x: MutableState<Float> = mutableStateOf(0f),
        var y: MutableState<Float> = mutableStateOf(0f),
        var z: MutableState<Float> = mutableStateOf(0f),
        var prevX: MutableState<Float> = mutableStateOf(0f),
        var prevY: MutableState<Float> = mutableStateOf(0f),
        var prevZ: MutableState<Float> = mutableStateOf(0f)
    )

    // 計算値
    companion object {
        var gyro = Val()
        var rad = Val()
        var angle = Val()
        var accel = Val()
        var linearAccel = Val()
        var velocity = Val()    // wip
        var position = Val()    // wip
//        var gvec = Val()

    }

    // 初期設定
    init {
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)

        repo.setInitValues()
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyro.x.value = event.values[0]
                gyro.y.value = event.values[1]
                gyro.z.value = event.values[2]

//                angle.x.value += repo.normalizeAngle(repo.integrate(gyro.prevX.value, gyro.x.value)/(Math.PI *10).toFloat())
//                angle.y.value += repo.normalizeAngle(repo.integrate(gyro.prevY.value, gyro.y.value)/(Math.PI *10).toFloat())
//                angle.z.value += repo.normalizeAngle(repo.integrate(gyro.prevZ.value, gyro.z.value)/(Math.PI *10).toFloat())
//                angle.x.value += repo.integrate(gyro.prevX.value, gyro.x.value)
//                angle.y.value += repo.integrate(gyro.prevY.value, gyro.y.value)
//                angle.z.value += repo.integrate(gyro.prevZ.value, gyro.z.value)

                gyro.prevX.value = gyro.x.value
                gyro.prevY.value = gyro.y.value
                gyro.prevZ.value = gyro.z.value
//                angle.prevX.value = angle.x.value
//                angle.prevY.value = angle.y.value
//                angle.prevZ.value = angle.z.value
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getQuaternionFromVector(currentQuat, event.values)

                val orientation = repo.quaternionToEuler(currentQuat)

                angle.x.value = orientation[0]
                angle.y.value = orientation[1]
                angle.z.value = orientation[2]
//                --------------------
//                repo.calcTone()

//                --------------------

                angle.prevX.value = angle.x.value
                angle.prevY.value = angle.y.value
                angle.prevZ.value = angle.z.value
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accel.x.value = event.values[0]
                accel.y.value = event.values[1]
                accel.z.value = event.values[2]
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                // delay normalじゃないとdtが短すぎる
                // m/s to cm/s
//                linearAccel.x.value = worldAc[0]
//                linearAccel.y.value = worldAc[1]
//                linearAccel.z.value = worldAc[2]
                linearAccel.x.value = event.values[0]
                linearAccel.y.value = event.values[1]
                linearAccel.z.value = event.values[2]


//                velocity.x.value += (repo.integrate(linearAccel.prevX.value, linearAccel.x.value))
//                velocity.y.value += (repo.integrate(linearAccel.prevY.value, linearAccel.y.value))
//                velocity.z.value += (repo.integrate(linearAccel.prevZ.value, linearAccel.z.value))
                velocity.x.value = repo.lowPassFilter(velocity.prevX.value, velocity.x.value, 0.7f)
                velocity.y.value = repo.lowPassFilter(velocity.prevY.value, velocity.y.value, 0.7f)
                velocity.z.value = repo.lowPassFilter(velocity.prevZ.value, velocity.z.value, 0.7f)

//                // フィルタの適用
//                position.z.value = velocity.z.value * dt.toFloat()
                position.z.value += repo.df(repo.integrate(velocity.prevZ.value, velocity.z.value))
//                position.z.value = repo.lowPassFilter(position.prevZ.value, position.z.value)
//                position.z.value = highPassFilter(position.prevZ.value, position.z.value)

                // 極端な位置になったらリセット
                if (abs(position.z.value) > 200f) position.z.value = 0f
                //--------------------
//                Log.d("v-v, angY", "${(abs(velocity.prevX.value) - abs(velocity.x.value)) + abs(velocity.prevX.value - velocity.x.value)}, \t ${angle.y.value}")
                // pitch shift
                if (abs(angle.y.value) > 20f && (abs(velocity.prevX.value - velocity.x.value)) + (velocity.prevX.value - velocity.x.value) > 50f) {
                    if (angle.y.value.sign == 1.0f) {
                        repo.pitchShift += 7
                    } else {
                        repo.pitchShift -= 7
                    }
                }
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
//            Sensor.TYPE_GRAVITY -> {
//            }
            Sensor.TYPE_PRESSURE -> {
                repo.currentTimestamp = event.timestamp
                repo.dt = (repo.currentTimestamp - repo.previousTimestamp) / 1_000_000_000.0 // ナノ秒を秒に変換

                if (repo.initPressure == null) {
                    repo.initPressure = event.values[0]
                }
            }
        }

//        Log.d("RotationVec To Euler", "Ang: ${repo.df(angle.x.value)}°,\t ${repo.df(angle.y.value)}°,\t ${repo.df(angle.z.value)}°")
        repo.previousTimestamp = repo.currentTimestamp
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }
}
