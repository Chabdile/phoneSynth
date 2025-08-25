package com.example.phonesynth.component

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import com.example.phonesynth.R
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream

class ToneRepository(private val context: Context) {

    init {
//        copyDefaultTones()
//        Log.d("tonerepo", "copyDefaultTones done")
    }

    private val audioDir = File(context.filesDir, "audio").apply { if (!exists()) mkdirs() }
//    val defaultTones = getDefaultLists()

//    private fun copyDefaultTones() {
//        //app dropped
//        val assetManager = context.assets
//        val assetFiles = assetManager.list("samples") ?: emptyArray()
//
//        assetFiles.forEach { fileName ->
//            val file = assetManager.open("samples/$fileName")
//            Log.d("tonerepo", "filename: ${fileName}: ${file}")
//            val destFile = File(audioDir, fileName)
//            Log.d("tonerepo", "defaultTones path: ${destFile.path}")
//            if (!destFile.exists()) {
//                file.use { input ->
//                    FileOutputStream(destFile).use { output ->
//                        input.copyTo(output)
//                    }
//                }
//            }
//        }
//    }
//
//    fun getDefaultLists(): List<String> {
//        return audioDir.list()?.toList() ?: emptyList()
//    }

//    fun savePreset(context: Context, fileName: String, content: String) {
//        val file = File(getDir(context, "presets"), fileName)
//        file.writeText(content) // 文字列を保存
//    }
//
//    fun loadPreset(context: Context, fileName: String): String? {
//        val file = File(getDir(context, "presets"), fileName)
//        return if (file.exists()) file.readText() else null
//    }
//
//    fun saveOption(context: Context, fileName: String, content: String) {
//        val file = File(getDir(context, "option"), fileName)
//        file.writeText(content)
//    }
//
//    fun loadOption(context: Context, fileName: String): String? {
//        val file = File(getDir(context, "option"), fileName)
//        return if (file.exists()) file.readText() else null
//    }



//    fun convertToPcm(uri: Uri): File? {
//        val inputPath = uri.path ?: return null
//        val outputFile = File(audioDir, "${System.currentTimeMillis()}.pcm")
//
//        val command = "-i $inputPath -ac 2 -ar 44100 -sample_fmt s16le -f s16le ${outputFile.absolutePath}"
//        val session = FFmpegKit.execute(command)
//
//        return if (session.returnCode.isSuccess) {
//            Log.d("FFmpeg", "Conversion successful: ${outputFile.absolutePath}")
//            outputFile
//        } else {
//            Log.e("FFmpeg", "Conversion failed: ${session.failStackTrace}")
//            null
//        }
//    }

//    fun convertWavToPcm(context: Context, rawResId: Int, outputFile: File) {
//        val inputStream = context.resources.openRawResource(rawResId)
//        Log.d("convertWavToPcm", "start inputStream")
//        inputStream.use { input ->
//            DataInputStream(BufferedInputStream(input)).use { dataInput ->
//                FileOutputStream(outputFile).use { output ->
//                    // WAVヘッダー（44バイト）を読み飛ばす
//                    dataInput.skip(44)
//
//                    // PCMデータを書き出す
//                    val buffer = ByteArray(1024)
//                    var bytesRead: Int
//                    while (dataInput.read(buffer).also { bytesRead = it } != -1) {
//                        output.write(buffer, 0, bytesRead)
//                    }
//                }
//            }
//        }
//        Log.d("convertWavToPcm", "end")
//    }

//    fun deleteTone(toneName: String) {
//        val file = File(audioDir, toneName)
//        if (file.exists()) file.delete()
//    }
//
//    fun renameTone(oldName: String, newName: String): String? {
//        val oldFile = File(audioDir, oldName)
//        val newFile = File(audioDir, newName)
//        return if (oldFile.exists() && !newFile.exists() && oldFile.renameTo(newFile)) newFile.name else null
//    }

//    fun getDir(context: Context, subDir: String): File {
//        val dir = File(audioDir, subDir)
//        if (!dir.exists()) dir.mkdirs() // 存在しない場合は作成
//        return dir
//    }

//    fun getFile(context: Context, rawResId: Int, subDir: String, fileName: String): File {
//        val dir = getDir(context, subDir) // 指定ディレクトリ取得
//        val outputFile = File(dir, fileName)
//
//        if (!outputFile.exists()) {
//            try {
//                context.resources.openRawResource(rawResId).use { inputStream ->
//                    FileOutputStream(outputFile).use { outputStream ->
//                        inputStream.copyTo(outputStream)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("getFile", "Error reading raw resource: ${e.message}")
//                // エラーが発生した場合、nullを返すか、適切なエラーハンドリングを行う
//                throw e
//            }
//        }
//
//        return outputFile
//    }

}
