package com.example.phonesynth.component

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.phonesynth.component.Sensors.Companion.accel
import com.example.phonesynth.component.Sensors.Companion.linearAccel
import com.example.phonesynth.component.Sensors.Companion.velocity
import com.example.phonesynth.component.Sensors.Companion.position
import com.example.phonesynth.component.Sensors.Companion.gyro
import com.example.phonesynth.component.Sensors.Companion.angle
import kotlin.math.abs
import com.example.phonesynth.component.Sensors.Val
import java.lang.Math.toDegrees
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.withSign

class Repository {
//
    var isSnap = true
    var isMuted = false

    var initPressure: Float? = null     // 気圧
    var pressure = mutableStateOf(0f)

// 音の3要素
    var volume = mutableFloatStateOf(0f)
    var tone = mutableFloatStateOf(0f)
    var articulation = mutableFloatStateOf(0f)

    // master pitch
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


    // ベクトルの回転：q * v * q⁻¹
    fun rotateVectorByQuaternion(v: FloatArray, q: FloatArray): FloatArray {
        val vQuat = floatArrayOf(0f, v[0], v[1], v[2])
        val qConj = quatConjugate(q)
        val rotatedQuat = quatMultiply(quatMultiply(q, vQuat), qConj)
        return floatArrayOf(rotatedQuat[1], rotatedQuat[2], rotatedQuat[3])
    }

    fun quaternionToEuler(q: FloatArray): FloatArray {
        val (w, x, y, z) = q
        val roll = atan2((2 * (w * x + y * z)).toDouble(), (1 - 2 * (x * x + y * y)).toDouble())
        val pitch = asin(2 * (w * y - z * x).toDouble())
        val yaw = atan2((2 * (w * z + x * y)).toDouble(), (1 - 2 * (y * y + z * z)).toDouble())

        return floatArrayOf(
            toDegrees(roll).toFloat(),
            toDegrees(pitch).toFloat(),
            toDegrees(yaw).toFloat()
        )
    }


    // クォータニオン乗算
    fun quatMultiply(a: FloatArray, b: FloatArray): FloatArray {
        val w1 = a[0]; val x1 = a[1]; val y1 = a[2]; val z1 = a[3]
        val w2 = b[0]; val x2 = b[1]; val y2 = b[2]; val z2 = b[3]
        return floatArrayOf(
            w1*w2 - x1*x2 - y1*y2 - z1*z2,
            w1*x2 + x1*w2 + y1*z2 - z1*y2,
            w1*y2 - x1*z2 + y1*w2 + z1*x2,
            w1*z2 + x1*y2 - y1*x2 + z1*w2
        )
    }

    // クォータニオン共役
    fun quatConjugate(q: FloatArray): FloatArray {
        return floatArrayOf(
            q[0], -q[1], -q[2], -q[3]
        )
    }

    fun quaternionFromTo(from: FloatArray, to: FloatArray): FloatArray {
        val v0 = normalize(from)
        val v1 = normalize(to)
        val d = dotProduct(v0, v1)

        return if (d >= 0.9999f) {
            floatArrayOf(0f, 0f, 0f, 1f) // 同じ向きなので回転なし
        } else if (d <= -0.9999f) {
            // 逆向き：直交軸で180度回転
            val axis = orthogonal(v0)
            quaternionFromAxisAngle(axis, PI.toFloat())
        } else {
            //Shoemake
            val axis = crossProduct(v0, v1)
            val s = sqrt((1 + d) * 2)
            val invs = 1 / s
            floatArrayOf(
                s * 0.5f,
                axis[0] * invs,
                axis[1] * invs,
                axis[2] * invs
            )
        }
    }

    fun quaternionFromAxisAngle(axis: FloatArray, rad: Float): FloatArray {
        val normAxis = axis //must already be normalized
        val halfAngle = rad / 2f
        val sinHalf = sin(halfAngle)
        return floatArrayOf(
            cos(halfAngle),
            normAxis[0] * sinHalf,
            normAxis[1] * sinHalf,
            normAxis[2] * sinHalf
        )
    }

    fun orthogonal(v: FloatArray): FloatArray {
        // v に直交するベクトルを見つける
        return when {
            abs(v[0]) < abs(v[1]) && abs(v[0]) < abs(v[2]) -> {
                // v[0] が最小の場合、x をゼロにして直交ベクトルを作る
                floatArrayOf(0f, -v[2], v[1])
            }
            abs(v[1]) < abs(v[0]) && abs(v[1]) < abs(v[2]) -> {
                // v[1] が最小の場合、y をゼロにして直交ベクトルを作る
                floatArrayOf(-v[2], 0f, v[0])
            }
            else -> {
                // v[2] が最小の場合、z をゼロにして直交ベクトルを作る
                floatArrayOf(-v[1], v[0], 0f)
            }
        }.let { normalize(it) } // 必ず正規化
    }

    fun dotProduct(a: FloatArray, b: FloatArray): Float {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2]
    }

    fun crossProduct(v1: FloatArray, v2: FloatArray): FloatArray {
        return floatArrayOf(
            v1[1]*v2[2] - v1[2]*v2[1],
            v1[2]*v2[0] - v1[0]*v2[2],
            v1[0]*v2[1] - v1[1]*v2[0]
        )
    }

    fun normalize(v: FloatArray): FloatArray {
        val len = vectorLength(v)
        return if (len > 1e-6f) {
            floatArrayOf(v[0]/len, v[1]/len, v[2]/len)
        } else {
            floatArrayOf(0f, 0f, 0f) // または例外にする
        }
    }

    fun vectorLength(v: FloatArray): Float {
        return sqrt(v[0].pow(2) + v[1].pow(2) + v[2].pow(2))
    }

    fun lowPassFilter(previousValue: Float, currentValue: Float, k: Float = 0.5f): Float {
        var value = 0.0f
        val prev = previousValue
        val curr = currentValue
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
        val prev = previousValue
        val curr = currentValue
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

    fun df2(value: Float): Float {
        val df = DecimalFormat("#.#")
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
        var note = semitones + 12 * 6   //midiでの音高(C4 = 60, C5 = 72)
        val octave = floor(note / 12f).toInt()

        if (isSnap) {
            //相対的な Major Scale
            if (note % 12 <= 4 && note % 2 == 1) {
                //ド〜ミの内、奇数番目 = 半音高い時
                note -= 1
            }
            if (note % 12 > 4 && note % 2 == 0) {
                //ファ〜シの内、偶数番目 = 半音低い時
                note += 1
            }
        }
//        Log.d("semitones: ", "${semitones}, \t ${pitchShift}, \t ${octave}, \t ${velocity.x.value},  \t ${dt}")

        return note - 12 * 6
    }

    fun calcTone() {
        initPressure?.let {
            //15度ずつで半音上昇
            val semitonesChange = (angle.x.value / 15).roundToInt()

            //発音される音程(Hz)
            //C5からの比(移動ドから移調も考慮)
            tone.floatValue = 2 * C4_FREQUENCY *
                    2.0.pow(
                        (setScale(semitonesChange + pitchShift) + transpose) / 12.0
                    ).toFloat()
        } ?: {
            tone.floatValue = previousTone
        }
//        tone.floatValue = lowPassFilter(previousTone, tone.floatValue)

        previousTone = tone.floatValue
        // 極端な音高になったらパラメータリセット
        if (tone.floatValue >= 20000f || tone.floatValue <= 20f) setInitValues()
    }

    // 音量を更新するメソッド
    fun calcVolume() {
        if (isMuted) {
            volume.floatValue = 0.0f
            return
        }

        val prev = angle.prevX.value
        val curr = angle.x.value
        val th = 5f
        //標準では水平が0なので、90度足して下向きを0に変換
        val d = abs(90 + df(lowPassFilter(prev, curr)))

        // 以下と同値のはず
//        volume.floatValue = when {
//            d <= th * 2.0f   -> 0.0f
//            d >= 180.0f - th -> 1.0f
//            else -> {
//                val t = (d - th * 2.0f) / (180.0f - th)
//                df(t.coerceIn(0.0f, 1.0f))
//            }
//        }
        previousVolume = volume.floatValue
        volume.floatValue = when (d) {
            in (0.0f..th * 2) -> 0.0f
            in (th * 2..180.0f - th) -> df((d - th * 2.0f) / (180.0f - th)).coerceIn(0.0f, 1.0f)
            in (180.0f - th..180.0f) -> 1.0f
            else -> 1f
        }
    }

    fun calcVolumeY() {
        if (isMuted) {
            volume.floatValue = 0.0f
            return
        }

        val prev = angle.prevY.value
        val curr = angle.y.value
        val th = 5f
        val d = abs(90 + df(lowPassFilter(prev, curr)))

        // 以下と同値のはず
//        volume.floatValue = when {
//            d <= th * 2.0f   -> 0.0f
//            d >= 180.0f - th -> 1.0f
//            else -> {
//                val t = (d - th * 2.0f) / (180.0f - th)
//                df(t.coerceIn(0.0f, 1.0f))
//            }
//        }
        previousVolume = volume.floatValue
        volume.floatValue = when (d) {
            in (0.0f..th * 2) -> 0.0f
            in (th * 2..180.0f - th) -> df((d - th * 2.0f) / (180.0f - th)).coerceIn(0.0f, 1.0f)
            in (180.0f - th..180.0f) -> 1.0f
            else -> 1f
        }
    }

    fun calcArticulation() {
        val prev = angle.prevX.value
        val curr = angle.x.value
        val th = 5f
        val d = abs(90 + df(lowPassFilter(prev, curr)))

        previousArticulation = articulation.floatValue
        articulation.floatValue = when (d) {
            in (0.0f..th) -> 0.0f
            in (th..180.0f - th) -> df((d - th) / (180.0f - th)).coerceIn(0.0f, 1.0f)
            in (180.0f - th..180.0f) -> 1.0f
            else -> 1f
        }
    }

    fun normalizeAngle(rad: Float): Float {
        val twoPi = (2 * PI).toFloat()
        var normalizedRad = rad % twoPi
        if (normalizedRad < 0) {
            normalizedRad += twoPi
        }
        return toDegrees(normalizedRad.toDouble()).toFloat()
    }

//    fun soundStop(bool: Boolean = false, k: Float = 0.8f) {
//        if (bool) {
//            volume.floatValue = 0.0f
////            if (abs(linearAccel.z.value) < 0.2f && abs(accel.z.value - 9.9f) < 0.03) {
////                angle = Val()
////            }
//        } else volume.floatValue = k
//    }

    // 現在の値を初期化
    fun setInitValues() {
        accel = Val()
        linearAccel = Val()
        velocity = Val()
        position = Val()
        gyro = Val()
        angle = Val()

        tone.floatValue = C4_FREQUENCY * 2
        volume.floatValue = 0.0f
        articulation.floatValue = 0.5f

        pitchShift = 0
        pressure.value = 1013f
        initPressure = null
    }
 }