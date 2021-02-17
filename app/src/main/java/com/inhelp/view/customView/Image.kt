package com.inhelp.view.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.inhelp.R


class Image : View {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
        this.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    mXdown = event.x
                    mYdown = event.y
                    mX = mXdown
                    mY = mYdown
                }
                MotionEvent.ACTION_MOVE -> {
                    mX = event.x - mXdown
                    mY = event.y - mYdown
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    rectf.set(rectf2)
                }
                else -> {}
            }
            true
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)


    }

    private var mX: Float = 0f
    private var mY: Float = 0f

    private var mXdown: Float = 0f
    private var mYdown: Float = 0f

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        this.background = ContextCompat.getDrawable(context, R.drawable.a)


    }

    val paint: Paint
    get(){
        val p = Paint()
        p.color = Color.RED
        p.strokeWidth = 10f
        return p
    }


    var rectf = RectF(0f, 0f, 0f + 100f, 0f + 100f)
    val rectf2 = RectF(0f, 0f, 0f + 100f, 0f + 100f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rectf2.left = rectf.left + mX
        rectf2.right = rectf.right + mX
        rectf2.top = rectf.top + mY
        rectf2.bottom = rectf.bottom + mY

        canvas.drawRect(rectf2, paint)
    }
}
