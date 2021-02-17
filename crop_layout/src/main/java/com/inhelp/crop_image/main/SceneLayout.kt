package com.inhelp.crop_image.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.core.view.drawToBitmap
import androidx.core.view.setMargins
import com.inhelp.crop_image.main.data.AspectRatio
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        overlayLayout.onTouch(event)
        overlayLayout.invalidate()
        return true
    }

    fun addOverlay(overlay: Overlay){

    }

    fun createCropOverlay(aspectRatio: AspectRatio?, isShowGrid: Boolean = false){
        overlayLayout.overlays.clear()
        overlayLayout.overlays.add(RectangleCropOverlay(context = context, aspectRatio = aspectRatio, isShowGrid = isShowGrid).apply {
            this.init(frame = cropImageView)
        })
        overlayLayout.invalidate()
    }

    fun makeCrop(sliceByAspectRatio: Boolean = false){
        val mainHandler = Handler(Looper.getMainLooper())
        val source = image ?: return
        thread {
                    val crop = overlayLayout.overlays.filterIsInstance<RectangleCropOverlay>().firstOrNull() ?: return@thread
                    val cropsPercent = crop.getCrops(sliceByAspectRatio = sliceByAspectRatio, originHeight = image?.height?.toFloat() ?: 0f, originWidth = image?.width?.toFloat() ?: 0f)

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

    companion object {

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(OVERLAY_SHAPE_NONE, OVERLAY_SHAPE_RECTANGLE, OVERLAY_SHAPE_CIRCLE, OVERLAY_SHAPE_CUSTOM)
        annotation class OverlayShape

        private const val OVERLAY_SHAPE_NONE = 0
        private const val OVERLAY_SHAPE_RECTANGLE = 1
        private const val OVERLAY_SHAPE_CIRCLE = 2
        private const val OVERLAY_SHAPE_CUSTOM = 3
    }

}