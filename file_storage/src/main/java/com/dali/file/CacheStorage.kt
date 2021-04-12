package com.dali.file

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException
import java.net.URLEncoder

object FileStorage {

    private const val STORAGE_NAME = "dali"


    private var dir: String = ""

    fun init(context: Context) {
        val appDir = File(context.cacheDir.path + File.separator + STORAGE_NAME)
        if (!appDir.exists() && !appDir.isDirectory) {
            appDir.mkdirs()
        }
        dir = appDir.path
    }

    fun writeToFile(fileName: String, data: String, dirName: String? = null) {
        try {
            val dirPath = makeDir(dirName)
            val outputFile = File(dirPath, fileName)
            outputFile.writeText(text = data, charset = Charsets.UTF_8)
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
            outputFile.delete()
        }
    }

    fun copyFileToDir(file: File, dirName: String, fileName: String){
        val dirPath = makeDir(dirName)
        val outputFile = File(dirPath, fileName)
        removeFile(fileName = fileName, dirName = dirName)
        file.copyTo(outputFile)
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