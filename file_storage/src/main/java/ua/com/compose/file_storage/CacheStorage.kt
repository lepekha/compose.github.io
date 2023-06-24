package ua.com.compose.file_storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileStorage {

    private var dir: String = ""

    fun init(context: Context) {
        dir = context.cacheDir.path
    }

    suspend fun Bitmap.writeToFile(fileName: String, dirName: String? = null, quality: Int = 100): Uri {
        val dirPath = makeDir(dirName)
        val outputFile = File(dirPath, fileName)
        removeFile(fileName = fileName, dirName = dirName)
        try {
            FileOutputStream(outputFile).use { out ->
                this.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputFile.toUri()
    }

    fun writeToFile(fileName: String, data: String, dirName: String? = null): File? {
        try {
            val dirPath = makeDir(dirName)
            val outputFile = File(dirPath, fileName)
            outputFile.writeText(text = data, charset = Charsets.UTF_8)
            return outputFile
        } catch (e: IOException) {
        }
        return null
    }

    fun writeToFile(file: File, targetFile: File) {
        try {
            targetFile.writeText(text = file.readText(Charsets.UTF_8), charset = Charsets.UTF_8)
        } catch (e: IOException) {
        }
    }

    fun readFromFile(fileName: String, dirName: String? = null): String {
        try {
            val dirPath = makeDir(dirName)
            val outputFile = File(dirPath, fileName)
            return outputFile.readText(Charsets.UTF_8)
        } catch (e: IOException) {
            return ""
        }
    }

//    fun readFilesFromDir(dirName: String): MutableList<String> {
//        val dirPath = makeDir(dirName)
//        return File(dirPath).listFiles()?.map { file -> file.readText(Charsets.UTF_8) }?.toMutableList() ?: mutableListOf()
//    }

    fun readFilesFromDir(dirName: String): MutableList<Uri> {
        val dirPath = makeDir(dirName)
        return File(dirPath).listFiles()?.map { file -> Uri.fromFile(file) }?.toMutableList()
                ?: mutableListOf()
    }

    fun readFilesNameFromDir(dirName: String): MutableList<String> {
        val dirPath = makeDir(dirName)
        return File(dirPath).listFiles()?.map { it.name }?.toMutableList() ?: mutableListOf()
    }

    fun removeFile(fileName: String, dirName: String? = null) {
        val dirPath = makeDir(dirName)
        val outputFile = File(dirPath, fileName)
        if (outputFile.exists()) {
            outputFile.absoluteFile.delete()
        }
    }

    fun removeFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }

    fun Context.copyFileToDir(file: File, dirName: String = "temp", fileName: String): Uri {
        val dirPath = makeDir(dirName)
        val outputFile = File(dirPath, fileName)
        removeFile(fileName = fileName, dirName = dirName)
        file.copyTo(outputFile)
        return outputFile.toUri()
    }

    fun clearDir(dirName: String) {
        val dirPath = makeDir(dirName)
        File(dirPath).deleteRecursively()
    }

    fun makeDir(dirName: String?): String {
        dirName ?: return dir
        val appDir = File(dir + File.separator + dirName)
        if (!appDir.exists() && !appDir.isDirectory) {
            appDir.mkdirs()
        }
        return appDir.path
    }
}