package com.example.phonesynth.ui

import com.example.phonesynth.SensorViewModel
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
//import org.nd4j.linalg.inverse.InvertMatrix
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2

class rotateMat {
    // x軸周りの回転行列
    private fun rotationMatrixX(theta: Float): INDArray {
        return Nd4j.create(floatArrayOf(
            1f, 0f, 0f,
            0f, cos(theta), -sin(theta),
            0f, sin(theta), cos(theta)
        ), intArrayOf(3, 3))
    }

    // y軸周りの回転行列
    private fun rotationMatrixY(theta: Float): INDArray {
        return Nd4j.create(floatArrayOf(
            cos(theta), 0f, sin(theta),
            0f, 1f, 0f,
            -sin(theta), 0f, cos(theta)
        ), intArrayOf(3, 3))
    }

    // z軸周りの回転行列
    private fun rotationMatrixZ(theta: Float): INDArray {
        return Nd4j.create(floatArrayOf(
            cos(theta), -sin(theta), 0f,
            sin(theta), cos(theta), 0f,
            0f, 0f, 1f
        ), intArrayOf(3, 3))
    }

    private fun multiplyMatrices(a: INDArray, b: INDArray): INDArray {
        return a.mmul(b)
    }

    // 回転行列からオイラー角を取得
    private fun getEulerAngles(rad: SensorViewModel.Vec): SensorViewModel.Vec {
        val eulerAngle = SensorViewModel.Vec()
        val sy: Float

        // 基準角度 (-90, 0, 0) の回転行列
        val baseRotationX = rotationMatrixX(Math.toRadians(-90.0).toFloat()) // X軸 -90度
        val baseRotationZ = rotationMatrixX(Math.toRadians(90.0).toFloat()) // X軸 -90度

        // 相対座標系での回転行列 (x, y, z)
        val rotationX = rotationMatrixX(rad.x)
        val rotationY = rotationMatrixY(rad.y)
        val rotationZ = rotationMatrixZ(rad.z)

        // 絶対座標系の回転行列を取得 (基準回転行列に相対回転行列を掛ける)
        var rotationMatrix = multiplyMatrices(baseRotationX, baseRotationZ)  // 基準回転行列 X軸
        rotationMatrix = multiplyMatrices(rotationMatrix, rotationY)     // Y軸
        rotationMatrix = multiplyMatrices(rotationMatrix, rotationZ)     // Z軸

        // 回転行列からオイラー角を計算
        sy = sqrt((rotationMatrix.getFloat(0, 0) * rotationMatrix.getFloat(0, 0) +
                rotationMatrix.getFloat(1, 0) * rotationMatrix.getFloat(1, 0)))

        val singular = sy < 1e-6f

        if (!singular) {
            eulerAngle.x = atan2(rotationMatrix.getFloat(2, 1), rotationMatrix.getFloat(2, 2))
            eulerAngle.y = atan2(-rotationMatrix.getFloat(2, 0), sy)
            eulerAngle.z = atan2(rotationMatrix.getFloat(1, 0), rotationMatrix.getFloat(0, 0))
        } else {
            eulerAngle.x = atan2(-rotationMatrix.getFloat(1, 2), rotationMatrix.getFloat(1, 1))
            eulerAngle.y = atan2(-rotationMatrix.getFloat(2, 0), sy)
            eulerAngle.z = 0f
        }

        return eulerAngle
    }
}