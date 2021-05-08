package ua.com.compose.view.customView.cropTool

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import ua.com.compose.utils.extension.dp

class CropImage(var imageWidth: Float, var imageHeight: Float) {
    var width = 100.dp
        set(value) {
            if ((value * horizontalSectionCount) <= imageWidth) {
                field = Math.max(100.dp, value)
            }
        }

    var height = 100.dp
        set(value) {
            if ((value * verticalSectionCount) <= imageHeight) {
                field = Math.max(100.dp, value)
            }
        }

    var verticalSectionCount = 2
        set(value) {
            if ((value * height) <= imageWidth) {
                field = value
            }
        }

    var horizontalSectionCount = 2
        set(value) {
            if ((value * width) <= imageWidth) {
                field = value
            }
        }
//        set(value) {
//            if ((value * width <= imageWidth) and
//                    (value * height <= imageHeight) and
//                    (((value * width * horizontalSectionCount) + xPosition) <= imageWidth) and
//                    (((value * height * verticalSectionCount) + yPosition) <= imageHeight)) {
//                field = value
//            }
//        }

    var xPosition = 0f
        set(value) {
            field = when {
                value < 0f -> 0f
                (value + (width * horizontalSectionCount)) > imageWidth -> imageWidth - (width * horizontalSectionCount)
                else -> value
            }
        }
    var yPosition = 0f
        set(value) {
            field = when {
                value < 0f -> 0f
                (value + (height * verticalSectionCount)) > imageHeight -> imageHeight - (height * verticalSectionCount)
                else -> value
            }
        }

    val paint: Paint
        get() {
            val p = Paint()
            p.color = Color.RED
            p.style = Paint.Style.STROKE
            p.strokeWidth = 5f
            return p
        }

    val horizontalRange: ClosedFloatingPointRange<Float>
        get() = xPosition..(xPosition + width * horizontalSectionCount)

    val verticalRange: ClosedFloatingPointRange<Float>
        get() = yPosition..(yPosition + height * verticalSectionCount)

    private val rectf = RectF(xPosition, yPosition, xPosition + width * horizontalSectionCount, yPosition + height * verticalSectionCount)

    fun setSize(width: Float, heigth: Float){
        val maxSize = Math.max(width, heigth)
        this.width = maxSize
        this.height = maxSize
    }

    fun setXYPosition(width: Float, heigth: Float){
        val maxSize = Math.max(width, heigth)
        this.xPosition = -maxSize
        this.yPosition = maxSize
    }


    fun draw(canvas: Canvas) {
        (1..horizontalSectionCount).forEach { horizontalSection ->
            (1..verticalSectionCount).forEach { verticalSection ->
                rectf.left = xPosition + ((horizontalSection - 1) * (width))
                rectf.right = xPosition + (horizontalSection * (width))
                rectf.top = yPosition + ((verticalSection - 1) * (height))
                rectf.bottom = yPosition + (verticalSection * (height))

                canvas.drawRect(rectf, paint)
            }
        }

    }
}