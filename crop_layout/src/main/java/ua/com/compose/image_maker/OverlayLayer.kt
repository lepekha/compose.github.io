package ua.com.compose.image_maker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toRect
import ua.com.compose.extension.vibrate

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
            overlays.forEach { overlay ->
                overlay.onTouch(e)
            }
    }

    fun onDoubleTap(e: MotionEvent): Boolean{
        overlays.forEach { overlay ->
            if(!overlay.onDoubleTap(e.x, e.y)){
                return false
            }
        }
        return true
    }

    fun onScaleBegin(x: Float, y: Float){
        overlays.forEach { overlay ->
            overlay.onScaleBegin(x, y)
        }
    }

    fun onScale(scaleFactor: Float){
        overlays.forEach { overlay ->
            overlay.onScale(scaleFactor)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val systemG = mutableListOf<RectF>()
        overlays.forEach { overlay ->
            overlay.onDraw(canvas = canvas)
            systemG.addAll(overlay.gestureExclusion)
        }
        if (Build.VERSION.SDK_INT >= 29){
            systemGestureExclusionRects = systemG.map { it.toRect() }
        }
    }
}
