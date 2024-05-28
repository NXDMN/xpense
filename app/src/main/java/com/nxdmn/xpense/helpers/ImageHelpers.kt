package com.nxdmn.xpense.helpers

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.ComponentActivity
import java.io.IOException


fun readImage(contentResolver: ContentResolver, uri: Uri?): Bitmap?{
    var bitmap: Bitmap? = null
    if(uri != null){
        try {
            contentResolver.apply{
                takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val source = ImageDecoder.createSource(contentResolver, uri)
            bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setTargetSampleSize(1) // shrinking by
                decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    return bitmap
}

fun readImageFromPath(context: Context, path: String): Bitmap?{
    if(path == "") return null
    var bitmap: Bitmap? = null
    val uri = Uri.parse(path)
    if(uri != null){
        try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            bitmap = ImageDecoder.decodeBitmap(source)
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

    return bitmap
}