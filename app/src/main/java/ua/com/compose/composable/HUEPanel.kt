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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.core.graphics.toRect
import androidx.core.graphics.withClip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sf.javaml.core.kdtree.KDTree
import ua.com.compose.data.ColorNames
import java.util.Arrays
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.TreeMap
import java.util.TreeSet
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import android.graphics.Color as AndroidColor

@Composable
fun HueBar(
    modifier: Modifier = Modifier,
    hue: Float,
    setColor: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    var _hue by remember {
        mutableStateOf(hue)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressOffset = remember {
        mutableStateOf(Offset.Unspecified)
    }

    if(_hue != hue) {
        _hue = hue
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
        val hueColors = IntArray((huePanel.width()).toInt())
        var hueLine = 0f
        for (i in hueColors.indices) {
            val _color = AndroidColor.HSVToColor(floatArrayOf(hueLine, 1f, 1f))
            hueColors[i] = _color
            val red = android.graphics.Color.red(_color).toDouble()
            val green = android.graphics.Color.green(_color).toDouble()
            val blue = android.graphics.Color.blue(_color).toDouble()
            tree.insert(doubleArrayOf(red, green, blue), i)
            hueLine += 360f / hueColors.size
        }

        val linePaint = Paint()
        linePaint.strokeWidth = 0F

        hueCanvas.withClip(containerPath) {
            for (i in hueColors.indices) {
                linePaint.color = hueColors[i]
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
            _hue = pointToHue(pressPos)
//            _color.value = AndroidColor.HSVToColor(floatArrayOf(pointToHue, 1f, 1f))
            setColor(_hue)
        }

        val center = pressOffset.value.takeIf { it.isSpecified }?.let { Offset(pressOffset.value.x, size.height/2) } ?: kotlin.run {
            val _color = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
            val red = android.graphics.Color.red(_color).toDouble()
            val green = android.graphics.Color.green(_color).toDouble()
            val blue = android.graphics.Color.blue(_color).toDouble()

            val index = (tree.nearest(doubleArrayOf(red, green, blue)) as? Int) ?: 0
            Offset(index.toFloat(), size.height/2)
        }

        drawCircle(
            Color.White,
            radius = size.height/2 - 2.dp.toPx(),
            center = center,
        )

        drawCircle(
            color = Color(AndroidColor.HSVToColor(floatArrayOf(_hue, 1f, 1f))),
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