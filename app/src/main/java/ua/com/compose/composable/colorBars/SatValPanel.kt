package ua.com.compose.composable.colorBars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asHSV
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.HSVColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.textColor
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.vibrate

@Composable
fun SatValBar(
    color: MutableState<IColor>,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    // Define minimum values for saturation and brightness
    val minSaturation = 0.01f
    val minBrightness = 0.01f

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    // Чекаємо на перший дотик
                    val down = awaitFirstDown()
                    view.vibrate(type = EVibrate.BUTTON)
                    val width = this.size.width.toFloat()
                    val height = this.size.height.toFloat()

                    // Обробка tap (натискання)
                    val newX = down.position.x.coerceIn(0f, width)
                    val newY = down.position.y.coerceIn(0f, height)

                    pressOffset = Offset(newX, newY)

                    val (newSaturation, newBrightness) = pointToSatVal(
                        x = newX,
                        y = newY,
                        width = width,
                        height = height
                    )

                    val hsv = color.value as HSVColor

                    // Clamp the saturation and brightness values
                    val clampedSaturation = newSaturation.coerceIn(minSaturation, 1f)
                    val clampedBrightness = newBrightness.coerceIn(minBrightness, 1f)

                    // Оновлюємо насиченість і яскравість при натисканні
                    color.value = hsv.copy(saturation = clampedSaturation, value = clampedBrightness)

                    // Обробка drag (перетягування)
                    drag(down.id) { change ->
                        val dragX = change.position.x.coerceIn(0f, width)
                        val dragY = change.position.y.coerceIn(0f, height)

                        pressOffset = Offset(dragX, dragY)

                        val (dragSaturation, dragBrightness) = pointToSatVal(
                            x = dragX,
                            y = dragY,
                            width = width,
                            height = height
                        )

                        // Clamp the values during drag
                        val clampedDragSaturation = dragSaturation.coerceIn(minSaturation, 1f)
                        val clampedDragBrightness = dragBrightness.coerceIn(minBrightness, 1f)

                        // Оновлюємо значення під час перетягування
                        color.value = hsv.copy(saturation = clampedDragSaturation, value = clampedDragBrightness)

                        change.consume() // Споживаємо зміни жесту
                    }
                }
            }
    ) {
        val widthPx = maxWidth
        val heightPx = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Extract hue, saturation, and lightness
            val hsv = color.value as HSVColor

            // Create gradients for saturation and brightness
            val hsvColor = Color.hsv(hue = hsv.hue, saturation = 1f, value = 1f)
            val satGradient = listOf(Color.White, hsvColor)
            val valGradient = listOf(Color.Transparent, Color.Black)

            // Draw saturation gradient (horizontal)
            drawRect(
                brush = Brush.horizontalGradient(satGradient),
                size = Size(widthPx.toPx(), heightPx.toPx())
            )

            // Draw brightness gradient (vertical)
            drawRect(
                brush = Brush.verticalGradient(valGradient),
                size = Size(widthPx.toPx(), heightPx.toPx())
            )

            // Draw selection indicator
            val center = if (pressOffset != Offset.Zero) pressOffset else satValToPoint(hsv.saturation, hsv.value, widthPx.toPx(), heightPx.toPx())

            val indicatorColor = hsv.textColor().asComposeColor()

            drawCircle(
                color = indicatorColor,
                radius = 12.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = indicatorColor,
                radius = 3.dp.toPx(),
                center = center
            )
        }
    }
}

private fun pointToSatVal(x: Float, y: Float, width: Float, height: Float): Pair<Float, Float> {
    val saturation = (x / width).coerceIn(0f, 1f)
    val value = (1f - y / height).coerceIn(0f, 1f)
    return saturation to value
}

private fun satValToPoint(saturation: Float, value: Float, width: Float, height: Float): Offset {
    val x = saturation * width
    val y = (1f - value) * height
    return Offset(x, y)
}