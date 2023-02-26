package com.mrjalal.sendlocationperiodically.data.util

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.reflect.KProperty

object NetworkFactory {
    private const val TAG = "NetworkFactory"

    fun post(url: String, body: String): String? {
        try {

            val urlConnection by lazy {
                URL(url).openConnection() as HttpURLConnection
            }

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.setRequestProperty("Accept", "application/json")
            urlConnection.doOutput = true

            val outputWriter = OutputStreamWriter(urlConnection.outputStream)
            outputWriter.write(body)
            outputWriter.flush()

            val inputStream = urlConnection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            urlConnection.disconnect()

            return response.toString()

        } catch (e: IOException) {
            Log.e(TAG, "Error posting to $url", e)
        }
        return null
    }
}