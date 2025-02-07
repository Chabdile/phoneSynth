package com.example.phonesynth.component

import androidx.compose.runtime.mutableStateOf
import com.example.phonesynth.component.Sensors.Companion.accel
import com.example.phonesynth.component.Sensors.Companion.linearAccel
import com.example.phonesynth.component.Sensors.Companion.velocity
import com.example.phonesynth.component.Sensors.Companion.position
import com.example.phonesynth.component.Sensors.Companion.gyro
import com.example.phonesynth.component.Sensors.Companion.angle
import kotlin.math.abs
import com.example.phonesynth.component.Sensors.Vec
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign

class Repository {
//
    var isSnap = true
//    var isClick = false

    var initPressure: Float? = null     // 気圧
    var pressure = mutableStateOf(0f)

// 音の3要素
    var volume = mutableStateOf(0f)
    var tone = mutableStateOf(0f)
    var articulation = mutableStateOf(0f)

    val A4_FREQUENCY = 440.0f
// 主音 = C4 * 2 = C5
    val C4_FREQUENCY = A4_FREQUENCY * 2.0.pow(-9.0 / 12.0).toFloat()
    var transpose = 0
    var pitchShift = 0

//平滑化パラメータ
    var previousTone = C4_FREQUENCY * 2
    var previousVolume = 0.8f
    var previousArticulation = 0.8f
    var previousPressure = 0.0f
//    private var alpha = 0.5f // スムージング係数

// timestamps
    var previousTimestamp: Long = 0L
    var currentTimestamp: Long = 0L
    var dt: Double = 0.0


    //class Repository(sensors: Sensors) {
    fun integrate(previousValue: Float, currentValue: Float): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.05f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        if (previousTimestamp != 0L) {
            // 台形による積分
            value = ((curr + prev) / 2 * dt.toFloat())
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
            value = ((curr - prev) / dt.toFloat())
//            value = df(value)
            if (abs(value) < th) value = 0.0f
        }
        return value
    }

    fun lowPassFilter(previousValue: Float, currentValue: Float, k: Float = 0.5f): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = (k * curr + (1 - k) * prev)
//        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    fun highPassFilter(previousValue: Float, currentValue: Float, k: Float = 0.5f): Float {
        var value = 0.0f
        var prev = previousValue
        var curr = currentValue
        val th = 0.02f
        //gate
//        if (abs(prev) < th) prev = 0.0f
//        if (abs(curr) < th) curr = 0.0f
        value = ((1 - k) * (value + curr - prev))
        value = df(value)
        if (abs(value) < th) value = 0.0f
        return value
    }

    // 小数点切り捨て
    fun df(value: Float): Float {
        val df = DecimalFormat("#.####")
        return df.format(value).toFloat()
    }

//    fun substituteVal(v: Val) {
//
//    }
//
//    fun substitutePrevVal(v: Val) {
//
//    }

    // 音程を指定スケールに補正
    fun setScale(semitones: Int = 0): Int {
        //c0を基準としてメジャースケールに調整。c0以下では効果がない
        //負数のピッチシフトに対応するために開始位置はC5
        var degree = semitones + 12 * 6   //midiでの音高(C4 = 60, C5 = 72)
        val octave = (degree / 12)

        if (isSnap) {
            //相対的な Major Scale
            if (degree % 12 <= 4 && degree % 2 == 1) {
                //ド〜ミの内、奇数番目 = 半音高い時
                degree -= 1
            }
            if (degree % 12 > 4 && degree % 2 == 0) {
                //ファ〜シの内、偶数番目 = 半音低い時
                degree += 1
            }
        }
//        Log.d("semitones: ", "${semitones}, \t ${pitchShift}, \t ${octave}, \t ${velocity.x.value},  \t ${dt}")

        return degree - 12 * 6
    }

    fun calcTone() {
        initPressure?.let {
//            val pressureDifference = initPressure!! - pressure.value
            //15度ずつで半音上昇
            val semitonesChange = (angle.x.value / 15).roundToInt()

            //発音される音程(Hz)
            //C5からの比(移動ドから移調も考慮)
            tone.value = 2 * C4_FREQUENCY *
                    2.0.pow(
                        (setScale(semitonesChange + pitchShift) + transpose) / 12.0
                    ).toFloat()
//            tone.value *= 2.0.pow(1 + transpose / 12.0).toFloat() * setScale(semitonesChange + pitchShift)
        } ?: {
            tone.value = previousTone
        }

        // 極端な音高になったらパラメータリセット
        if (tone.value >= 20000f || tone.value <= 20f) setInitValues()

        //更新
//        tone.value = lowPassFilter(previousTone, tone.value)
        previousTone = tone.value
    }

    // 角度を実世界と一致させる
    fun normalizeAngle(rad: Float): Float {
        var normalizedRad = (rad % (2 * Math.PI) / 10) // Normalize to 0 - 2π radians
        if (abs(normalizedRad) >= (2 * Math.PI)) {
            normalizedRad = normalizedRad.sign * (2 * Math.PI) // Ensure positive value
        }
        return Math.toDegrees(normalizedRad)
            .toFloat() // Convert to degrees and normalize to 0 - 360 degrees
    }

//    // 音量を更新するメソッド
//    fun calcVolume() {
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

    fun pressStopButton(bool: Boolean = false) {
        if (bool == true) {
            volume.value = 0.0f
            if (abs(linearAccel.z.value) < 0.2f && abs(accel.z.value - 9.9f) < 0.03) {
                angle = Vec()
            }
        } else volume.value = 0.8f
    }

    // 現在の値を初期化
    fun setInitValues() {
        accel = Vec()
        linearAccel = Vec()
        velocity = Vec()
        position = Vec()
        gyro = Vec()
        angle = Vec()

//        volume.value = 0f
        tone.value = 2 * C4_FREQUENCY
        pitchShift = 0
        articulation.value = 0.5f
        pressure.value = 1013f
        initPressure = null
    }
//}
 }