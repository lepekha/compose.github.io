package ua.com.compose.composable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import androidx.core.graphics.withClip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sf.javaml.core.kdtree.KDTree
import ua.com.compose.extension.asComposeColor
import ua.com.compose.colors.asRGBdecimal
import ua.com.compose.colors.data.Color
import ua.com.compose.colors.data.HSVColor
import kotlin.math.max

@Composable
fun HueBar(
    modifier: Modifier = Modifier,
    hsv: HSVColor,
    setColor: (HSVColor) -> Unit
) {
    val scope = rememberCoroutineScope()
    var _hsv by remember {
        mutableStateOf(hsv)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressOffset = remember {
        mutableStateOf(Offset.Unspecified)
    }

    if(_hsv != hsv) {
        _hsv = hsv
        pressOffset.value = Offset.Unspecified
    }

    val tree = KDTree(3)

    Canvas(
        modifier = modifier.emitDragGesture(interactionSource)
    ) {
        val drawScopeSize = size
        val bitmap = Bitmap.createBitmap(max(1, size.width.toInt()), max(1, size.height.toInt()), Bitmap.Config.ARGB_8888)
        val hueCanvas = Canvas(bitmap)
        val containerPath = Path()
        val huePanel = RectF(0f, 3.dp.toPx(), bitmap.width.toFloat(), bitmap.height.toFloat() - 3.dp.toPx())
        val radius = 7.dp.toPx()
        containerPath.addRoundRect(huePanel, radius, radius, Path.Direction.CW)
        val hueColors = arrayOfNulls<Color>(huePanel.width().toInt())
        var hueLine = 0f
        for (i in hueColors.indices) {
            val _color = _hsv.copy(hue = hueLine, saturation = 1f, value = 1f)
            hueColors[i] = _color
            val rgb = _color.asRGBdecimal()
            tree.insert(doubleArrayOf(rgb.red.toDouble(), rgb.green.toDouble(), rgb.blue.toDouble()), i)
            hueLine += 360f / hueColors.size
        }

        val linePaint = Paint()
        linePaint.strokeWidth = 0F

        hueCanvas.withClip(containerPath) {
            for (i in hueColors.indices) {
                linePaint.color = hueColors[i]?.intColor ?: 0
                hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
            }
        }

        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }

        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset.value = Offset(pressPos, 0f)
            _hsv = _hsv.copy(hue = pointToHue(pressPos))
            setColor(_hsv)
        }

        val center = pressOffset.value.takeIf { it.isSpecified }?.let { Offset(pressOffset.value.x, size.height/2) } ?: kotlin.run {
            val _color = _hsv.copy(saturation = 1f, value = 1f).asRGBdecimal()
            val index = (tree.nearest(doubleArrayOf(_color.red.toDouble(), _color.green.toDouble(), _color.blue.toDouble())) as? Int) ?: 0
            Offset(index.toFloat(), size.height/2)
        }

        drawCircle(
            androidx.compose.ui.graphics.Color.White,
            radius = size.height/2 - 2.dp.toPx(),
            center = center,
        )

        drawCircle(
            color = _hsv.copy(saturation = 1f, value = 1f).asComposeColor(),
            radius = size.height/2 - 5.dp.toPx(),
            center = center,
        )
    }
}

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

fun Modifier.emitDragGesture(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectDragGestures { input, _ ->
            scope.launch {
                interactionSource.emit(PressInteraction.Press(input.position))
            }
        }
    }.clickable(interactionSource, null) {
    }
}

fun DrawScope.drawBitmap(
    bitmap: Bitmap,
    panel: RectF
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel.toRect(),
            null
        )
    }
}