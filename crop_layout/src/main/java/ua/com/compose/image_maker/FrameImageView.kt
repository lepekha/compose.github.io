package ua.com.compose.image_maker

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import ua.com.compose.extension.bitmapPosition
import ua.com.compose.extension.dp
import ua.com.compose.extension.getColorFromAttr


class FrameImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    interface OnImageChangeListener {
        fun imageSampleChange(bitmap: Bitmap?)
    }

    private var onImageChangeListener: OnImageChangeListener? = null

    fun setImageChangeListener(onImageChangeListener: OnImageChangeListener?) {
        this.onImageChangeListener = onImageChangeListener
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        this.post {
            onImageChangeListener?.imageSampleChange(bitmap = this.drawable?.toBitmap())
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        this.post {
            val dr = this.drawable ?: return@post
            onImageChangeListener?.imageSampleChange(bitmap = this.drawable?.toBitmap())
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        this.post {
            onImageChangeListener?.imageSampleChange(bitmap = this.drawable?.toBitmap())
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        this.post {
            onImageChangeListener?.imageSampleChange(bitmap = this.drawable?.toBitmap())
        }
    }

    private val mLinePaint by lazy {
        Paint().apply {
            this.style = Paint.Style.FILL_AND_STROKE
            this.color = context.getColorFromAttr(R.attr.color_5)
            this.strokeWidth = 1.dp
        }
    }

    private val mBorderPaint by lazy {
        Paint().apply {
            this.style = Paint.Style.STROKE
            this.color = context.getColorFromAttr(R.attr.color_5)
            this.strokeWidth = 1.dp
        }
    }

    var isFrameEnable = false
        set(value) {
            field = value
            invalidate()
        }

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(isFrameEnable){
            drawFrame(canvas)
        }
    }

    private fun drawFrame(canvas: Canvas){
        val ret = this.bitmapPosition()
        rect.set(ret[0].toFloat(), ret[1].toFloat(), (ret[0] + ret[2]).toFloat(), (ret[1] + ret[3]).toFloat())

        val widthPart = rect.width() / 4
        val heightPart = rect.height() / 4
        (1..3).forEach {
            canvas.drawLine(rect.left + (widthPart * it), rect.top, rect.left + (widthPart * it), rect.bottom, mLinePaint)
            canvas.drawLine(rect.left, rect.top + (heightPart * it), rect.right, rect.top + (heightPart * it), mLinePaint)
        }

        canvas.drawRect(rect, mBorderPaint)
    }
}