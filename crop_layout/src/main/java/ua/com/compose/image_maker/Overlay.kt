package ua.com.compose.image_maker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

abstract class Overlay constructor(val context: Context){
  internal var frame: Rect? = null
  internal val gestureExclusion = mutableListOf<Rect>()
  internal var onAnimate: () -> Unit = {}
  internal abstract fun onDraw(canvas: Canvas)
  internal abstract fun init(frame: Rect)
  internal open fun onTouch(e: MotionEvent) {}
  internal open fun onTouchUp(e: MotionEvent) {}
  internal open fun onTouchMove(e: MotionEvent) {}
  internal open fun onDoubleTap(x: Float, y: Float): Boolean { return true }
  internal open fun onScaleBegin(x: Float, y: Float): Boolean { return true }
  internal open fun onScale(scaleFactor: Float) {}
}
