package com.nxdmn.xpense.helpers

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder

fun write(context: Context, fileName: String, data: String){
    try{
        val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.write(data)
        outputStreamWriter.close()
    }catch (e: Exception){
        Log.e("Error: ", "Cannot read file: $e")
    }
}

fun read(context: Context, fileName: String): String{
    var str = ""
    try{
        val inputStream = context.openFileInput(fileName)
        if(inputStream != null){
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val partialStr = StringBuilder()
            var done = false
            while(!done){
                var line = bufferedReader.readLine()
                done = (line == null)
                if (line != null) partialStr.append(line)
            }
            inputStream.close()
            str = partialStr.toString()
        }
    }catch (e: FileNotFoundException){
        Log.e("Error: ", "File not found: $e")
    }catch (e: Exception){
        Log.e("Error: ", "Cannot read file: $e")
    }

    return str
}

fun exists(context: Context, fileName: String): Boolean{
    val file = context.getFileStreamPath(fileName)
    return file.exists()
}