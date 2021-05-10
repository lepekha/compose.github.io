package ua.com.compose.image_maker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.extension.*
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
        defaultCropImageView.adjustViewBounds = true
        defaultCropImageView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER)
        defaultCropImageView.setPaddingLeft(15.dp.toInt())
        defaultCropImageView.setPaddingRight(15.dp.toInt())
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
            val ret = cropImageView.bitmapPosition()
            val frameRect = Rect(ret[0], ret[1], (ret[0] + ret[2]), (ret[1] + ret[3]))
            this.init(frame = frameRect)
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

    }

    fun setImage(uri: Uri, onFinish: () -> Unit = {}) {
        overlayLayout.overlays.clear()
        overlayLayout.invalidate()

        Glide.with(context.applicationContext).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(0.1f).listener(
                object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        cropImageView.setImageDrawable(resource)
                        cropImageView.takeIf { isFirstResource }?.post {
                            onFinish()
                        }

                        return true
                    }
                }
        ).into(cropImageView)

        Glide.with(context.applicationContext)
                .asBitmap()
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        image = resource
                    }
                })

    }

  fun addListener(listener: Listener) {
      listeners.add(listener)
  }

}