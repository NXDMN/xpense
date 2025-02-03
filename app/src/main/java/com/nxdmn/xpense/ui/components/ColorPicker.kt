package com.nxdmn.xpense.ui.components

import android.graphics.Bitmap
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import kotlinx.coroutines.launch

/**
 * @param color The 32-bit ARGB color int
 * @param onColorSelected The callback with 32-bit ARGB color int as the selected value
 */
@Composable
fun ColorPicker(color: Long, onColorSelected: (Long) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        var hsv by remember {
            val hsv = floatArrayOf(0f, 0f, 0f)
            android.graphics.Color.colorToHSV(color.toInt(), hsv)
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2])
            )
        }

        SaturationAndValuePanel(
            hue = hsv.first,
            onSaturationAndValueSelected = { saturation, value ->
                hsv = Triple(
                    hsv.first,
                    saturation,
                    value
                )
                onColorSelected(
                    //Color.hsv(hsv.first, hsv.second, hsv.third).toArgb().toLong()
                    android.graphics.Color.HSVToColor(
                        floatArrayOf(
                            hsv.first,
                            hsv.second,
                            hsv.third
                        )
                    ).toLong()
                )
            })

        HuePanel { color ->
            hsv = Triple(color, hsv.second, hsv.third)
            onColorSelected(
                //Color.hsv(hsv.first, hsv.second, hsv.third).toArgb().toLong()
                android.graphics.Color.HSVToColor(
                    floatArrayOf(
                        hsv.first,
                        hsv.second,
                        hsv.third
                    )
                ).toLong()
            )
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(color))
        )
    }
}

@Composable
fun SaturationAndValuePanel(hue: Float, onSaturationAndValueSelected: (Float, Float) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    var saturation: Float
    var value: Float
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .emitDragGesture(interactionSource)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val drawScopeSize = size
        val cornerRadius = 12.dp.toPx()

        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val saturationAndValueCanvas = android.graphics.Canvas(bitmap)
        val saturationAndValuePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val rgb = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val saturationShader = LinearGradient(
            saturationAndValuePanel.left,
            saturationAndValuePanel.top,
            saturationAndValuePanel.right,
            saturationAndValuePanel.top,
            -0x1,
            rgb,
            Shader.TileMode.CLAMP,
        )
        val valueShader = LinearGradient(
            saturationAndValuePanel.left,
            saturationAndValuePanel.top,
            saturationAndValuePanel.left,
            saturationAndValuePanel.bottom,
            -0x1,
            -0x1000000,
            Shader.TileMode.CLAMP,
        )

        saturationAndValueCanvas.drawRoundRect(
            saturationAndValuePanel,
            cornerRadius,
            cornerRadius,
            Paint().apply {
                shader = ComposeShader(valueShader, saturationShader, PorterDuff.Mode.MULTIPLY)
            })

        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, saturationAndValuePanel.toRect(), null)
        }

        fun pointToSatVal(pointX: Float, pointY: Float): Pair<Float, Float> {
            val width = saturationAndValuePanel.width()
            val height = saturationAndValuePanel.height()
            val x = when {
                pointX < saturationAndValuePanel.left -> 0f
                pointX > saturationAndValuePanel.right -> width
                else -> pointX - saturationAndValuePanel.left
            }
            val y = when {
                pointY < saturationAndValuePanel.top -> 0f
                pointY > saturationAndValuePanel.bottom -> height
                else -> pointY - saturationAndValuePanel.top
            }
            val saturationPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y
            return saturationPoint to valuePoint
        }

        scope.launch {
            interactionSource.interactions.collect { interaction ->
                (interaction as? PressInteraction.Press)?.pressPosition?.let {
                    val pressPositionOffset = Offset(
                        it.x.coerceIn(0f..drawScopeSize.width),
                        it.y.coerceIn(0f..drawScopeSize.height)
                    )

                    pressOffset = pressPositionOffset
                    val (satPoint, valuePoint) = pointToSatVal(
                        pressPositionOffset.x,
                        pressPositionOffset.y
                    )
                    saturation = satPoint
                    value = valuePoint
                    onSaturationAndValueSelected(saturation, value)
                }
            }
        }

        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = pressOffset,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = pressOffset,
        )
    }
}

@Composable
fun HuePanel(onColorSelected: (Float) -> Unit) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    Canvas(
        modifier = Modifier
            .height(40.dp)
            .width(300.dp)
            .clip(RoundedCornerShape(50))
            .emitDragGesture(interactionSource)
    ) {
        val drawScopeSize = size

        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = android.graphics.Canvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val hueColors = IntArray((huePanel.width()).toInt())
        var hue = 0f
        for (i in hueColors.indices) {
            hueColors[i] = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
            hue += 360f / hueColors.size
        }

        val linePaint = Paint()
        linePaint.strokeWidth = 0F
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
        }

        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, huePanel.toRect(), null)
        }

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }

        scope.launch {
            interactionSource.interactions.collect { interaction ->
                (interaction as? PressInteraction.Press)?.pressPosition?.let {
                    val pressPos = it.x.coerceIn(0f..drawScopeSize.width)
                    pressOffset = Offset(pressPos, 0f)
                    val selectedHue = pointToHue(pressPos)
                    onColorSelected(selectedHue)
                }
            }
        }

        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(pressOffset.x, size.height / 2),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

private fun Modifier.emitDragGesture(interactionSource: MutableInteractionSource): Modifier =
    composed {
        val scope = rememberCoroutineScope()
        pointerInput(Unit) {
            detectDragGestures { change, _ ->
                scope.launch {
                    interactionSource.emit(PressInteraction.Press(change.position))
                }
            }
        }.clickable(interactionSource, null) { }
    }

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        color = 0xFF808080,
        onColorSelected = {}
    )
}