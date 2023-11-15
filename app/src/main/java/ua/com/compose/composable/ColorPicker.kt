package ua.com.compose.composable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColor
import android.graphics.Color as AndroidColor



@Composable
fun SatValPanel(
    modifier: Modifier = Modifier,
    hsv: Triple<Float, Float, Float>,
    setSatVal: (Float, Float) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    var sat: Float = hsv.second
    var value: Float = hsv.third
    var hue by remember { mutableFloatStateOf(hsv.first) }
    val pressOffset = remember {
        mutableStateOf(Offset.Unspecified)
    }
    if(hue != hsv.first) {
        hue = hsv.first
        pressOffset.value = Offset.Unspecified
    }

    val containerPath = Path()

    Canvas(
        modifier = modifier
            .emitDragGesture(interactionSource)
            .clip(RoundedCornerShape(16.dp))
    ) {
        val cornerRadius = 16.dp.toPx()
        val satValSize = size
        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas().apply {
            this.setBitmap(bitmap)
        }
        var isDark = value < 0.7 || sat > 0.3
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        containerPath.rewind()
        val rgb = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP
        )
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x1000000, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(
            satValPanel,
            cornerRadius,
            cornerRadius,
            Paint().apply {
                shader = ComposeShader(
                    valShader,
                    satShader,
                    PorterDuff.Mode.MULTIPLY
                )
            }
        )
        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel
        )

        fun pointToSatVal(pointX: Float, pointY: Float): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()
            val x = when {
                pointX < satValPanel.left -> 0f
                pointX > satValPanel.right -> width
                else -> pointX - satValPanel.left
            }
            val y = when {
                pointY < satValPanel.top -> 0f
                pointY > satValPanel.bottom -> height
                else -> pointY - satValPanel.top
            }
            val satPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y
            return satPoint to valuePoint
        }

        fun satValToPoint(sat: Float, value: Float): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()
            val x = sat * width
            val y = (1f - value) * height
            val pointX = satValPanel.left + x
            val pointY = satValPanel.top + y
            return pointX to pointY
        }
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionOffset = Offset(
                pressPosition.x.coerceIn(0f..satValSize.width),
                pressPosition.y.coerceIn(0f..satValSize.height)
            )

            pressOffset.value = pressPositionOffset
            val (satPoint, valuePoint) = pointToSatVal(pressPositionOffset.x, pressPositionOffset.y)
            sat = satPoint
            value = valuePoint
            isDark = value < 0.7 || sat > 0.3
            setSatVal(sat, value)
        }
        val center = pressOffset.value.takeIf { it.isSpecified } ?: satValToPoint(sat, value).let { Offset(it.first, it.second) }

        val color = Color(Color.White.toArgb().takeIf { isDark } ?: ColorUtils.setAlphaComponent(Color.Black.toArgb(), 190))
        drawCircle(
            color = color,
            radius = 12.dp.toPx(),
            center = center,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = center,
        )

    }
}