package com.nxdmn.xpense.helpers

import androidx.compose.ui.graphics.Color
import kotlin.math.pow

fun Color.isLight(): Boolean {
    val r = if (red <= 0.03928f) red / 12.92f else ((red + 0.055f) / 1.055f).pow(2.4f)
    val g = if (green <= 0.03928f) green / 12.92f else ((green + 0.055f) / 1.055f).pow(2.4f)
    val b = if (blue <= 0.03928f) blue / 12.92f else ((blue + 0.055f) / 1.055f).pow(2.4f)

    val luminance = 0.2126f * r + 0.7152f * g + 0.0722f * b
    return luminance > 0.5
}