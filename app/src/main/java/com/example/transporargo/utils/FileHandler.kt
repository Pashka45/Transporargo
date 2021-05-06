package com.example.transporargo.utils

import android.app.Application
import android.content.Context
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileHandler(private val context: Application): Cloneable {

    var defaultText = ""

    @Throws(IOException::class)
    fun readFile(fileName: String): String {
        return try {
            val inputStreamReader = InputStreamReader(context.openFileInput(fileName))
            val fileContent = inputStreamReader.readText()
            inputStreamReader.close()
            fileContent
        } catch (e: IOException) {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(defaultText)
            outputStreamWriter.close()
            defaultText
        }
    }

    @Throws(IOException::class)
    fun writeFile(fileName: String, text: String) {
        val outputStreamWriter =
            OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.write(text)
        outputStreamWriter.close()
    }

    public override fun clone(): Any {
        return super.clone()
    }
}