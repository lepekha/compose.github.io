package ua.com.compose.other_color_pick.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ColorGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val colors: MutableList<Int> = mutableListOf()

    fun setColors(newColors: List<Int>) {
        colors.clear()
        colors.addAll(newColors)
        invalidate() // Оновлюємо вью для перерисування
    }

    private val paint = Paint()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val numColors = colors.size
        if (numColors == 0) return

        val squareSize = width / numColors // Розмір кожного квадратика

        // Ініціалізуємо фарбу для малювання квадратиків

        paint.style = Paint.Style.FILL

        for (i in 0 until numColors) {
            paint.color = colors[i]
            val left = i * squareSize
            val top = 0
            val right = left + squareSize
            val bottom = height
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}