package com.inhelp.crop_image.main

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.setMargins
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.extension.dp
import kotlin.concurrent.thread

class SceneLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface Listener

    interface CropListener: Listener {
        fun onCrop(bitmaps: List<Bitmap>)
    }

    private val cropImageView: SceneImageView
    private val overlayLayout: OverlayLayer = OverlayLayer(context)
    private val listeners = mutableListOf<Listener>()

    var image: Bitmap? = null

    init {
        val defaultCropImageView = SceneImageView(context, null, 0)
        defaultCropImageView.id = -1
        defaultCropImageView.scaleType = ImageView.ScaleType.FIT_XY
        defaultCropImageView.adjustViewBounds = true
        defaultCropImageView.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER).apply {
            this.setMargins(15.dp.toInt())
        }
        addView(defaultCropImageView, 0)
        cropImageView = defaultCropImageView
        addView(overlayLayout)
    }

    private val gestureListener by lazy { GestureDetector(context,  object : GestureDetector.SimpleOnGestureListener(){
        override fun onDoubleTap(e: MotionEvent): Boolean {
            overlayLayout.onDoubleTap(e)
            return true
        }
    })
    }

    private val scaleGestureDetector by lazy { ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            overlayLayout.onScale(detector.scaleFactor)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            overlayLayout.onScaleBegin(x = detector.focusX, y = detector.focusY)
            return super.onScaleBegin(detector)
        }
    })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureListener.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        overlayLayout.onTouch(event)
        overlayLayout.invalidate()
        return true
    }

    fun addOverlay(overlay: Overlay){

    }

    fun createCropOverlay(ratio: Ratio, isShowGrid: Boolean = false){
        val lastRect = overlayLayout.overlays.filterIsInstance<RectangleCropOverlay>().firstOrNull()?.getCropRect()
        overlayLayout.overlays.clear()
        overlayLayout.overlays.add(RectangleCropOverlay(context = context, ratio = ratio, isSliceByGrid = isShowGrid, oldRect = lastRect).apply {
            this.init(frame = cropImageView)
            this.onAnimate = {
                overlayLayout.invalidate()
            }
        })
        overlayLayout.invalidate()
    }

    fun makeCrop(){
        val mainHandler = Handler(Looper.getMainLooper())
        val source = image ?: return
        thread {
                    val crop = overlayLayout.overlays.filterIsInstance<RectangleCropOverlay>().firstOrNull() ?: return@thread
                    val cropsPercent = crop.getCrops(originHeight = image?.height?.toFloat() ?: 0f, originWidth = image?.width?.toFloat() ?: 0f)

                    val bms = cropsPercent.map { cropPosition ->  Bitmap.createBitmap(source, cropPosition.left.toInt(), cropPosition.top.toInt(), (cropPosition.right - cropPosition.left).toInt(), (cropPosition.bottom - cropPosition.top).toInt()) }
                    mainHandler.post {
                        listeners.filterIsInstance<CropListener>().forEach {
                            it.onCrop(bms)
                        }
                    }
            }
    }

    fun setBitmap(bitmap: Bitmap) {
        image = bitmap
        cropImageView.setImageBitmap(bitmap)
        cropImageView.requestLayout()
        cropImageView.invalidate()
    }

  fun addListener(listener: Listener) {
      listeners.add(listener)
  }

}