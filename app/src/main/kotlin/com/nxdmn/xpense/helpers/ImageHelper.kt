package com.nxdmn.xpense.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.IOException
import androidx.core.net.toUri

fun readImageFromPath(context: Context, path: String): ImageBitmap? {
    if (path == "") return null
    var bitmap: Bitmap? = null
    val uri = path.toUri()
    try {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.setTargetSampleSize(2)
        }
        bitmap.prepareToDraw()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return bitmap?.asImageBitmap()
}