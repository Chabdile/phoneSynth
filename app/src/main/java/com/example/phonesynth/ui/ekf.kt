package com.example.phonesynth.ui
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.inverse.InvertMatrix
import kotlin.math.cos
import kotlin.math.sin

class ekf {

    // 状態遷移関数 f(x, u): 状態xと入力uに基づいて次の状態を計算
    fun f(x: INDArray, u: INDArray): INDArray {
        val u_x = u.getFloat(0, 0)
        val u_y = u.getFloat(1, 0)
        val u_z = u.getFloat(2, 0)
        val c1 = cos(x.getFloat(0, 0))
        val s1 = sin(x.getFloat(0, 0))
        val c2 = cos(x.getFloat(1, 0))
        val s2 = sin(x.getFloat(1, 0))

        val newX = Nd4j.create(floatArrayOf(
            x.getFloat(0, 0) + u_x + u_y * s1 * s2 / c2 + u_z * c1 * s2 / c2,
            x.getFloat(1, 0) + u_y * c1 - u_z * s1,
            x.getFloat(2, 0) + u_y * s1 / c2 + u_z * c1 / c2
        ), intArrayOf(3, 1))

        return newX
    }

    // 観測モデル h(x): 状態xから観測される値を計算
    fun h(x: INDArray): INDArray {
        return Nd4j.create(floatArrayOf(
            1f, 0f, 0f,
            0f, 1f, 0f
        ),intArrayOf(2, 3)).mmul(x)
    }

    // 状態予測
    fun predictX(x: INDArray, u: INDArray): INDArray {
        return f(x, u)
    }

    // 共分散予測
    fun predictP(P: INDArray, F: INDArray, Q: INDArray): INDArray {
        return F.mmul(P).mmul(F.transpose()).add(Q)
    }

    // 状態遷移行列 F の計算
    fun calcF(x: INDArray, u: INDArray): INDArray {
        val u_x = u.getFloat(0, 0)
        val u_y = u.getFloat(1, 0)
        val u_z = u.getFloat(2, 0)
        val c1 = cos(x.getFloat(0, 0))
        val s1 = sin(x.getFloat(0, 0))
        val c2 = cos(x.getFloat(1, 0))
        val s2 = sin(x.getFloat(1, 0))

        val F = Nd4j.create(floatArrayOf(
            1f + u_y * c1 * s2 / c2 - u_z * s1 * s2 / c2,
            u_y * s1 / (c2 * c2) + u_z * c1 / (c2 * c2), 0f,
            -u_y * s1 - u_z * c1, 1f, 0f,
            u_y * c1 / c2 - u_z * s1 / c2,
            u_y * s1 * s2 / (c2 * c2) + u_z * c1 * s2 / (c2 * c2), 1f
        ), intArrayOf(3, 3))

        return F
    }

    // 観測行列 H の計算
    fun calcH(): INDArray {
        return Nd4j.create(floatArrayOf(
            1f, 0f, 0f,
            0f, 1f, 0f
        ),intArrayOf(2, 3))
    }

    // 観測更新 y_res の計算
    fun updateYRes(z: INDArray, x: INDArray): INDArray {
        return z.sub(h(x))
    }

    // 共分散行列 S の更新
    fun updateS(P: INDArray, H: INDArray, R: INDArray): INDArray {
        return H.mmul(P).mmul(H.transpose()).add(R)
    }

    // カルマンゲイン K の計算
    fun updateK(P: INDArray, H: INDArray, S: INDArray): INDArray {
        return P.mmul(H.transpose()).mmul(InvertMatrix.invert(S, false))
    }

    // 状態 x の更新
    fun updateX(x: INDArray, y_res: INDArray, K: INDArray): INDArray {
        return x.add(K.mmul(y_res))
    }

    // 共分散 P の更新
    fun updateP(P: INDArray, H: INDArray, K: INDArray): INDArray {
        val I = Nd4j.eye(3)
        return I.sub(K.mmul(H)).mmul(P)
    }

    // 拡張カルマンフィルタ (EKF) の実行
    fun ekf(
        x: INDArray, u: INDArray, z: INDArray,
        P: INDArray
    ): Pair<INDArray, INDArray> {
        val R = Nd4j.create(floatArrayOf(
            0.001f, 0f,
            0f, 0.001f
        ), intArrayOf(2, 2))
        val Q = Nd4j.create(floatArrayOf(
            0.001f, 0f, 0f,
            0f, 0.001f, 0f,
            0f, 0f, 0.001f
        ), intArrayOf(3, 3))
        // 予測ステップ
        val F = calcF(x, u)
        var xPred = predictX(x, u)
        val H = calcH()
        var PPred = predictP(P, F, Q)

        // 更新ステップ
        val yRes = updateYRes(z, xPred)
        val S = updateS(PPred, H, R)
        val K = updateK(PPred, H, S)
        xPred = updateX(xPred, yRes, K)
        PPred = updateP(PPred, H, K)

        return Pair(xPred, PPred)
    }

}