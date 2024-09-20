package ua.com.compose.composable.colorBars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.colorHSLOf
import ua.com.compose.colors.colorRGBdecimalOf
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.textColor
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.vibrate

@Composable
fun SaturationBar(
    color: MutableState<IColor>,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var pressOffset by remember { mutableStateOf<Offset?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    // Чекаємо на перший дотик
                    val down = awaitFirstDown()
                    view.vibrate(type = EVibrate.BUTTON)
                    val width = this.size.width
                    val radiusPx = this.size.height / 2f

                    // Обробка tap (натискання)
                    val newX = down.position.x.coerceIn(radiusPx, width.toFloat() - radiusPx)
                    val newSaturation = ((newX - radiusPx) / (width - 2 * radiusPx)).coerceIn(0f, 1f)

                    // Оновлюємо насиченість
                    val hsl = color.value as HSLColor
                    color.value = hsl.copy(saturation = newSaturation)
                    pressOffset = Offset(newX, this.size.height / 2f)

                    // Обробка drag (перетягування)
                    drag(down.id) { change ->
                        val newDragX = change.position.x.coerceIn(radiusPx, width.toFloat() - radiusPx)
                        val newDragSaturation = ((newDragX - radiusPx) / (width - 2 * radiusPx)).coerceIn(0f, 1f)

                        // Оновлюємо насиченість під час перетягування
                        color.value = hsl.copy(saturation = newDragSaturation)
                        pressOffset = Offset(newDragX, this.size.height / 2f)
                        change.consume() // Споживаємо зміни жесту
                    }
                }
            }
    ) {
        val widthPx = maxWidth
        val radiusPx = maxHeight / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Отримуємо поточний колір у форматі HSL
            val hsl = color.value as HSLColor

            // Створюємо градієнт для насиченості, зберігаючи відтінок і світлість постійними
            val saturationColors = (0..100).map { s ->
                Color.hsl(hue = hsl.hue, saturation = s / 100f, lightness = hsl.lightness)
            }

            // Малюємо фон з градієнтом
            drawRoundRect(
                brush = Brush.horizontalGradient(saturationColors),
                topLeft = Offset(0f, 0f),
                size = Size(widthPx.toPx(), maxHeight.toPx()),
                cornerRadius = CornerRadius(radiusPx.toPx(), radiusPx.toPx())
            )

            // Позиція поточної насиченості
            val currentSaturationX = (hsl.saturation * (widthPx.toPx() - 2 * radiusPx.toPx())) + radiusPx.toPx()

            // Малюємо індикатор
            val centerX = pressOffset?.x ?: currentSaturationX

            // Гарантуємо, що індикатор не виходить за межі бара з урахуванням радіусу
            val clampedX = centerX.coerceIn(radiusPx.toPx(), widthPx.toPx() - radiusPx.toPx())

            // Малюємо зовнішній білий круговий індикатор
            drawCircle(
                color = hsl.textColor().asComposeColor(),
                radius = radiusPx.toPx() - 3.dp.toPx(),
                center = Offset(clampedX, radiusPx.toPx())
            )

            // Малюємо внутрішній кольоровий індикатор
            drawCircle(
                color = hsl.asComposeColor(),
                radius = radiusPx.toPx() - 5.dp.toPx(),
                center = Offset(clampedX, radiusPx.toPx())
            )
        }
    }
}