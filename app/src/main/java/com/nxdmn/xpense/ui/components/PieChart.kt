package com.nxdmn.xpense.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartModel(
    val value: Float,
    val color: Color,
)

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    charts: List<ChartModel>,
    size: Dp = 200.dp,
    strokeWidth: Dp = 16.dp,
    text: String = ""
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(text = AnnotatedString(text))
    val textSize = textLayoutResult.size

    Canvas(
        modifier = modifier
            .size(size)
            .padding(12.dp)
    ) {
        var startAngle = 0f
        var sweepAngle: Float

        charts.forEach {
            sweepAngle = (it.value / 100) * 360
            drawArc(
                color = it.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Square,
                    join = StrokeJoin.Round
                )
            )
            startAngle += sweepAngle
        }

        drawText(
            textMeasurer, text,
            topLeft = Offset(
                (this.size.width - textSize.width) / 2f,
                (this.size.height - textSize.height) / 2f
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    val charts = listOf(
        ChartModel(value = 20f, color = Color.Red),
        ChartModel(value = 30f, color = Color.Yellow),
        ChartModel(value = 40f, color = Color.Green),
        ChartModel(value = 10f, color = Color.Blue),
    )
    PieChart(charts = charts, text = "Test")
}