package com.example.travelplanner.base.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.Completable
import io.reactivex.Single
import java.io.*
import java.lang.reflect.Type

class StorageManager(val context: Context, private val gson: Gson) {

    private val rootDir: String = context.filesDir.absolutePath + "/"

    fun <T> load(fileName: String, type: Type): Single<T> {
        return Single.fromCallable { loadFromFile<T>(fileName, type) }
    }

    fun <T> loadAsset(fileName: String, type: Type): Single<T> {
        return Single.fromCallable { loadFromAssets<T>(fileName, type) }
    }

    @SuppressLint("CheckResult")
    fun save(fileName: String, data: Any) {
        Completable.fromCallable { saveToFile(data, fileName) }.subscribe({}, { it.printStackTrace() })
    }

    private fun <T> loadFromFile(fileName: String, type: Type): T {
        var result: T? = null

        try {
            val reader = InputStreamReader(
                FileInputStream(File(rootDir + fileName)))
            result = gson.fromJson<T>(reader, type)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return result ?: throw NoSuchElementException("No data")
    }

    private fun <T> loadFromAssets(fileName: String, type: Type): T {
        var result: T? = null

        try {
            val reader = InputStreamReader(context.assets.open(fileName))
            result = gson.fromJson<T>(reader, type)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return result ?: throw Exception("no data")
    }

    private fun saveToFile(data: Any, fileName: String): Boolean {
        var result = false

        val file = File(rootDir + fileName)

        var osw: OutputStreamWriter? = null
        try {
            if (!file.exists()) {
                file.createNewFile()
            }

            osw = OutputStreamWriter(FileOutputStream(file))

            gson.toJson(data, osw)

            osw.flush()

            result = true
        } catch (e: IOException) {
            Log.e("Logger", "IOException writing file", e)
        } finally {
            if (osw != null) {
                try {
                    osw.close()
                } catch (e: IOException) {
                    // do nothing
                }

            }
        }

        return result
    }
}