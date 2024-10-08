package ua.com.compose.dialogs
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.compose.colors.analogous
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asHSV
import ua.com.compose.colors.colorHSLOf
import ua.com.compose.colors.colorHSVOf
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.shades
import ua.com.compose.colors.splitComplementary
import ua.com.compose.colors.tints
import ua.com.compose.colors.tones
import ua.com.compose.data.enums.EColorSchemeType
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.asIColor
import ua.com.compose.extension.vibrate
import kotlin.math.*


@Composable
fun ColorWheel(
    modifier: Modifier = Modifier,
    color: MutableState<IColor>,
    scheme: EColorSchemeType,
    count: Int = 5,
    onColorsSelected: (List<IColor>) -> Unit = {}
) {
    val view = LocalView.current
    // State to track hue, saturation, and selected color
    var selectedHue by remember { mutableFloatStateOf((color.value as HSLColor).hue) }
    var selectedSaturation by remember { mutableFloatStateOf((color.value as HSLColor).saturation) }
    val selectedLightness by remember {
        derivedStateOf {
            (color.value as HSLColor).lightness
        }
    }

    // Calculate the color scheme based on the selected hue, saturation, and the provided scheme type
    val colorSchemeColors = remember(selectedHue, selectedSaturation, selectedLightness, scheme, count) {
        scheme.calculateScheme(selectedHue, selectedSaturation, (color.value as HSLColor).lightness, count)
    }

    // Notify the caller about the selected color scheme
    LaunchedEffect(colorSchemeColors) {
        onColorsSelected(colorSchemeColors)
    }

    Canvas(modifier = modifier
        .pointerInput(Unit) {
            awaitEachGesture {
                // Чекаємо на перший дотик
                val down = awaitFirstDown()
                view.vibrate(type = EVibrate.BUTTON)

                val canvasWidth = size.width
                val canvasHeight = size.height
                val canvasCenter = Offset(canvasWidth / 2f, canvasHeight / 2f)
                val minDimension = min(canvasWidth, canvasHeight)
                val touchX = down.position.x - canvasCenter.x
                val touchY = down.position.y - canvasCenter.y
                val distance = sqrt(touchX.pow(2) + touchY.pow(2))

                val angle = (atan2(touchY, touchX).toDegrees() + 360) % 360
                val saturation = (distance / (minDimension / 2)).coerceIn(0f, 1f)

                selectedHue = angle
                selectedSaturation = saturation

                // Обробка drag (перетягування)
                drag(down.id) { change ->
                    val touchX = change.position.x - canvasCenter.x
                    val touchY = change.position.y - canvasCenter.y
                    val distance = sqrt(touchX.pow(2) + touchY.pow(2))

                    // Calculate hue and saturation from touch
                    val angle = (atan2(touchY, touchX).toDegrees() + 360) % 360
                    val saturation = (distance / (minDimension / 2)).coerceIn(0f, 1f)

                    selectedHue = angle
                    selectedSaturation = saturation

                    change.consume() // Споживаємо зміни жесту
                }
            }
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val minDimension = min(canvasWidth, canvasHeight)
        val radius = minDimension / 2f
        val center = Offset(canvasWidth / 2, canvasHeight / 2)

        // Create a list of hue colors for the sweep gradient (from red to red)
        val hueColors = List(360) { angle ->
            Color.hsv(angle.toFloat(), 1f, 1f)
        }

        // Sweep gradient for hue
        drawCircle(
            brush = Brush.sweepGradient(
                colors = hueColors,
                center = center
            ),
            radius = radius,
            style = Fill
        )

        // Radial gradient for saturation (from white in the center to transparent at the edge)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = center,
                radius = radius,
                tileMode = TileMode.Clamp
            ),
            radius = radius,
            style = Fill
        )

        // Draw cursors for the selected color scheme colors
        colorSchemeColors.reversed().forEachIndexed { index, color ->
            val hue = (color as HSLColor).hue
            val saturation = (color as HSLColor).saturation // Use the saturation of the color itself
            val selectionX = center.x + saturation * radius * cos(hue.toRadians())
            val selectionY = center.y + saturation * radius * sin(hue.toRadians())

            val divider = if(index == colorSchemeColors.count() - 1) {
                1f
            } else {
                1.4f
            }

            val content = if(index == colorSchemeColors.count() - 1) {
                Stroke(3.dp.toPx())
            } else {
                Fill
            }

            drawCircle(
                color = Color.Black.copy(alpha = 0.6f),
                radius = 7 * radius / 100  / divider,
                center = Offset(selectionX, selectionY),
                style = content
            )
//
//            drawCircle(
//                color = color.copy(lightness = 0.5f).asComposeColor(),
//                radius = 10.dp.toPx() / divider,
//                center = Offset(selectionX, selectionY),
//                style = Fill
//            )
        }
    }
}

private fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()
private fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()