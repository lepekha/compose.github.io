package com.inhelp.crop_image.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

abstract class Overlay constructor(val context: Context){
  internal var frame: View? = null
  internal abstract fun onDraw(canvas: Canvas)
  internal abstract fun init(frame: View)
  internal abstract fun onTouchDown(x: Float, y: Float)
  internal abstract fun onTouchUp(x: Float, y: Float)
  internal abstract fun onTouchMove(x: Float, y: Float)
}
