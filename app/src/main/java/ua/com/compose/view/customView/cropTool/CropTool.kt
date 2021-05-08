package ua.com.compose.view.customView.cropTool

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import ua.com.compose.R
import android.view.GestureDetector
import android.view.ScaleGestureDetector


class CropTool : View {

    private var mCropImage: CropImage = CropImage(0f, 0f)
    private val mSaveProportial = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    private lateinit var mMoveDetector: GestureDetector
    private lateinit var mScaleDetector: ScaleGestureDetector


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mCropImage = CropImage(this.width.toFloat(), this.height.toFloat())
        init(attrs, 0)
        mMoveDetector = GestureDetector(context, MyGestureListener())
        mScaleDetector = ScaleGestureDetector(context, MyScaleListener())
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        this.background = ContextCompat.getDrawable(context, R.drawable.a)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(event)

        return mMoveDetector.onTouchEvent(event)
    }

    internal inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            mCropImage.xPosition = mCropImage.xPosition - distanceX
            mCropImage.yPosition = mCropImage.yPosition - distanceY
            invalidate()
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent): Boolean {
            return (e.x in mCropImage.horizontalRange) and (e.y in mCropImage.verticalRange)
        }
    }


    internal inner class MyScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private var addWidth = 0f
        private var addHeight = 0f

        private var oldWidth = 0f
        private var oldHeight = 0f

        private var oldX = 0f
        private var oldY = 0f

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            addWidth = detector.currentSpanX
            addHeight = detector.currentSpanY

            oldWidth = mCropImage.width
            oldHeight = mCropImage.height

            oldX = mCropImage.xPosition
            oldY = mCropImage.yPosition
            return super.onScaleBegin(detector)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {

            if(mSaveProportial){
                mCropImage.setSize( oldWidth + (detector.currentSpanX - addWidth), oldHeight + (detector.currentSpanY - addHeight))

                val max = Math.max((detector.currentSpanX - addWidth), (detector.currentSpanY - addHeight))
                mCropImage.xPosition = oldX - max
                mCropImage.yPosition = oldY - max
            }else{
                mCropImage.width = oldWidth + (detector.currentSpanX - addWidth)
                mCropImage.height = oldHeight + (detector.currentSpanY - addHeight)

                mCropImage.xPosition = oldX - (detector.currentSpanX - addWidth)
                mCropImage.yPosition = oldY - (detector.currentSpanY - addHeight)
            }
            invalidate()
            return false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCropImage.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCropImage.imageWidth = w.toFloat()
        mCropImage.imageHeight = h.toFloat()
    }

}
