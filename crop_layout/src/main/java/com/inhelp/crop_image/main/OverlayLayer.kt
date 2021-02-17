package com.inhelp.crop_image.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.inhelp.extension.vibrate

class OverlayLayer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    init {
        this.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    val overlays = mutableListOf<Overlay>()

    fun onTouch(e: MotionEvent){
        if(e.action == MotionEvent.ACTION_DOWN){
            overlays.forEach { overlay ->
                overlay.onTouchDown(e.x, e.y)
            }
        }

        if(e.action == MotionEvent.ACTION_UP){
            overlays.forEach { overlay ->
                overlay.onTouchUp(e.x, e.y)
            }
        }

        if(e.action == MotionEvent.ACTION_MOVE){
            overlays.forEach { overlay ->
                overlay.onTouchMove(e.x, e.y)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        overlays.forEach { overlay ->
            overlay.onDraw(canvas = canvas)
        }
    }
}
